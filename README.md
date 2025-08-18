## Development

### 주요 구현사항
<details>
<summary> API 호출 및 에러처리 </summary>  

### 1. API 호출
- Retrofit + Coroutine 을 통한 API 호출
- Repository Pattern 을 사용하여, 데이터 소스를 추상화(API 통신, 로컬 DB 접근)하여 일관된 데이터 접근 인터페이스 제공 및 관심사 분리를 실현
```kotlin
// service
interface BookSearchService {
    @GET("search/book")
    suspend fun searchBook(
        @Query("query") query: String,
        @Query("sort") sort: String = "accuracy",
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20,
    ): SearchBookResponse
}

// repository 구현체 
internal class DefaultBookRepository @Inject constructor(
    private val service: BookSearchService,
    private val favoritesDao: FavoritesDao,
) : BookRepository {

    override suspend fun searchBook(
        query: String,
        sort: String,
        page: Int,
        size: Int,
    ) = service.searchBook(
        query = query,
        sort = sort,
        page = page,
        size = size,
    ).toModel()

    override fun searchFavoritesByTitle(query: String) = favoritesDao.searchFavoritesByTitle(query)
        .map { entities -> entities.map { it.toModel() } }

    override suspend fun insertBook(book: Book) {
        favoritesDao.insertFavorite(book.toEntity())
    }

    override suspend fun deleteBook(isbn: String) {
        favoritesDao.deleteFavorite(isbn)
    }

    override val favoriteBooks: Flow<List<Book>> = favoritesDao.getAllFavorites()
        .map { entities -> entities.map { it.toModel() } }
}
```
### 2. 에러 처리 
#### 2-1.  Coroutine 취소로 인해 발생하는 CancellationException 처리 
- API 호출 실패 관련 Exception 과 분리하여 CoroutineException(CancellationException)을 별도 처리(Coroutine 취소는 정상적인 제어 흐름의 일부로, 에러 상황이 아님, API Exception 은 실제 에러 상황)
- CancellationException 을 다시 던져, 상위 Coroutine에 전파함으로써, Coroutine 취소에 대한 적절한 처리를 보장(Coroutine 실행을 중단 시킴 -> 이 예외를 다시 던짐으로써, 취소 신호가 Coroutine 계층 구조를 따라 상위로 전파됨) 
```kotlin
import kotlin.coroutines.cancellation.CancellationException

inline fun <T> cancellableRunCatching(block: () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (cancellationException: CancellationException) {
        throw cancellationException
    } catch (exception: Exception) {
        Result.failure(exception)
    }
}
```
</details>

<details>
<summary> UseCase 도입 </summary>  
    
### 1. Clean Architecture 에서 Domain 모듈 
- [클라이언트 아키텍처에 대한 단상 - '서버'가 진짜 '도메인' 아닐까?](https://thdev.tech/architecture/2025/08/17/Clean-Android/) 해당 글과 같은 주장에 공감하는 입장이지만, 클린 아키텍처 에선 구글 권장 아키텍처와 다르게 Domain 이 필수이기 때문에, Domain 모듈과 UseCase를 도입함.
- 단, Repository 의 함수를 포워딩하는 UseCase의 경우 불필요한 뎁스를 늘리기만 하고, UseCase 도입의 가치가 없다고 생각하여 비즈니스 로직을 포함하여 UseCase를 도입
  -> UseCase를 통해 비즈니스 로직을 추상화하고, 여러 뷰모델에서 공통으로 사용하는 비즈니스 로직을 공통화하여, 뷰모델의 복잡도를 감소 및 갓 뷰모델이 되지 않도록 막음
```kotlin
class CombineBooksWithFavoritesUseCase @Inject constructor(
    private val repository: BookRepository,
) {
    operator fun invoke(booksFlow: Flow<List<Book>>): Flow<List<Book>> {
        return combine(
            booksFlow,
            repository.favoriteBooks,
        ) { books, favoriteBooks ->
            books.map { book ->
                val isFavorite = favoriteBooks.any { it.isbn == book.isbn }
                book.copy(isFavorite = isFavorite)
            }
        }
    }
}

class GetFavoriteBooksUseCase @Inject constructor(
    private val repository: BookRepository,
) {
    operator fun invoke(query: String, sortType: FavoritesSortType): Flow<List<Book>> {
        val booksFlow = if (query.isBlank()) {
            repository.favoriteBooks
        } else {
            repository.searchFavoritesByTitle(query)
        }

        return booksFlow.map { books ->
            when (sortType) {
                FavoritesSortType.TITLE_ASC -> books.sortedBy { it.title }
                FavoritesSortType.TITLE_DESC -> books.sortedByDescending { it.title }
            }
        }
    }
}

enum class FavoritesSortType(val label: String) {
    TITLE_ASC("오름차순(제목)"),
    TITLE_DESC("내림차순(제목)"),
    ;

    fun toggle(): FavoritesSortType {
        return when (this) {
            TITLE_ASC -> TITLE_DESC
            TITLE_DESC -> TITLE_ASC
        }
    }
}

class SearchBooksUseCase @Inject constructor(
    private val repository: BookRepository,
) {
    suspend operator fun invoke(
        query: String,
        sort: String,
        page: Int,
        size: Int,
        currentBooks: List<Book> = emptyList(),
    ): Result<SearchResult> {
        return cancellableRunCatching {
            val searchResult = repository.searchBook(query, sort, page, size)

            val newBooks = if (page == 1) {
                searchResult.documents
            } else {
                currentBooks + searchResult.documents
            }

            SearchResult(
                books = newBooks,
                isEnd = searchResult.meta.isEnd,
                totalCount = searchResult.meta.totalCount,
                nextPage = page + 1,
            )
        }
    }
}

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: BookRepository,
) {
    suspend operator fun invoke(book: Book): Boolean {
        val favoriteBooks = repository.favoriteBooks.first()
        val isCurrentlyFavorite = favoriteBooks.any { it.isbn == book.isbn }

        return if (isCurrentlyFavorite) {
            repository.deleteBook(book.isbn)
            false
        } else {
            repository.insertBook(book)
            true
        }
    }
}
``` 

</details>
    
<details>
<summary> 즐겨찾기에 추가된 도서, 검색 화면에 반영 </summary> 
    
- Flow combine 연산자를 사용하여, API 를 통해 호출한 booksFlow 와, 즐겨찾기로 추가한 favoriteBooks 의 변화를 구독
- combine 된 Flow 들 중 어느 하나라도 새로운 값을 emit 하면, transform 람다 함수가 호출됨 -> 즐겨찾기 추가, 삭제를 실시간으로 반영 

```kotlin
// CombineBooksWithFavoritesUseCase
    
class CombineBooksWithFavoritesUseCase @Inject constructor(
    private val repository: BookRepository,
) {
    operator fun invoke(booksFlow: Flow<List<Book>>): Flow<List<Book>> {
        return combine(
            booksFlow,
            repository.favoriteBooks,
        ) { books, favoriteBooks ->
            books.map { book ->
                val isFavorite = favoriteBooks.any { it.isbn == book.isbn }
                book.copy(isFavorite = isFavorite)
            }
        }
    }
}

data class SearchResult(
    val books: List<Book>,
    val isEnd: Boolean,
    val totalCount: Int,
    val nextPage: Int,
)
```
</details>

<details>
<summary> Recompotion 최적화 </summary>  
    
### 1. 안정성 문제 진단 [compose-metrics & compose-reports](https://developer.android.com/develop/ui/compose/performance/stability/diagnose?hl=ko)
- compose-metrics, compose-reports 를 이용한 class 및 composable 함수의 stable 여부 판정 
```kotlin
internal fun Project.configureCompose(
    extension: CommonExtension<*, *, *, *, *, *>,
) {
    extension.apply {
        dependencies {
            implementation(platform(libs.androidx.compose.bom))
            implementation(libs.bundles.androidx.compose)
            debugImplementation(libs.androidx.compose.ui.tooling)
        }

        configure<ComposeCompilerGradlePluginExtension> {
            includeSourceInformation.set(true)

            metricsDestination.file("build/composeMetrics")
            reportsDestination.file("build/composeReports")

            stabilityConfigurationFiles.addAll(
                project.layout.projectDirectory.file("stability.config.conf"),
            )
        }

        tasks.withType<KotlinCompile>().configureEach {
            compilerOptions {
                freeCompilerArgs.addAll(
                    buildComposeMetricsParameters(),
                )
            }
        }
    }
}

private fun Project.buildComposeMetricsParameters(): List<String> {
    val metricParameters = mutableListOf<String>()
    val enableMetricsProvider = project.providers.gradleProperty("enableComposeCompilerMetrics")
    val relativePath = projectDir.relativeTo(rootDir)
    val buildDir = layout.buildDirectory.get().asFile
    val enableMetrics = (enableMetricsProvider.orNull == "true")
    if (enableMetrics) {
        val metricsFolder = buildDir.resolve("compose-metrics").resolve(relativePath)
        metricParameters.add("-P")
        metricParameters.add("plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" + metricsFolder.absolutePath)
    }

    val enableReportsProvider = project.providers.gradleProperty("enableComposeCompilerReports")
    val enableReports = (enableReportsProvider.orNull == "true")
    if (enableReports) {
        val reportsFolder = buildDir.resolve("compose-reports").resolve(relativePath)
        metricParameters.add("-P")
        metricParameters.add("plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" + reportsFolder.absolutePath)
    }
    return metricParameters.toList()
}
```
gradle.properties
```
enableComposeCompilerMetrics=true
enableComposeCompilerReports=true
```

```
./gradlew assembleDebug -PenableComposeCompilerMetrics=true -PenableComposeCompilerReports=true
```

### 2. [Kotlin Immutable Collection](https://github.com/Kotlin/kotlinx.collections.immutable) Library 적용
- 표준 컬렉션 클래스 (List, Set, Map) 는 Unstable 
- `val set: Set<String> = mutableSetOf("foo")` 처럼 선언 타입은 immutable 한 Set 이지만, 구현은 mutable 할 수 있으므로, compose-compiler 가 안정하다 판단할 수 없음
- Kotlin Immutable Collection 을 사용하여(변경 불가능한 컬렉션으로 변환하여) stable 판정을 받아낼 수 있음  
```kotlin
// FavoritesViewModel
@OptIn(ExperimentalCoroutinesApi::class)
val favoriteBooks: StateFlow<ImmutableList<BookUiModel>> = _uiState
    .flatMapLatest { state ->
        getFavoriteBooksUseCase(
            query = state.searchQuery,
            sortType = state.sortType,
        ).map { books ->
            books.map { it.toUiModel().copy(isFavorites = true) }.toImmutableList()
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = persistentListOf(),
    )

// FavoritesScreen
@Composable
internal fun FavoritesScreen(
    innerPadding: PaddingValues,
    uiState: FavoritesUiState,
    favoriteBooks: ImmutableList<BookUiModel>,
    onAction: (FavoritesUiAction) -> Unit,
) { ... }
```

### 3. [Compose-Stable-Marker](https://github.com/skydoves/compose-stable-marker) Library 적용 
- compose 모듈이 아닌 모듈에서 선언된 class 는 compose-compiler 로 부터 unstable 판정을 받음
- compose-stable-marker 라이브러리를 compose 모듈이 아닌 모듈에 주입하면, @Stable, @Immutable annotation 을 해당 class 에 붙혀줄 수 있음
- 결과적으로 compose 모듈에서 compose 모듈이 아닌 모듈의 class 를 참조하여도 stable 판정을 받을 수 있게 됨

:core:common 모듈 내 클래스
```kotlin
import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Stable
@Serializable
data class BookUiModel(
    val title: String = "",
    val contents: String = "",
    val url: String = "",
    val isbn: String = "",
    val datetime: String = "",
    val authors: List<String> = listOf(),
    val publisher: String = "",
    val translators: List<String> = listOf(),
    val price: String = "",
    val salePrice: String = "",
    val thumbnail: String = "",
    val status: String = "",
    val isFavorites: Boolean = false,
)
```
</details>

### 비고 
<details>
<summary> Paging3 라이브러리를 사용하지 않은 이유 </summary>  

기존에 Paging3 라이브러리를 사용해보면서 불편하다고 느꼈던 몇몇 이유가 존재
1. API 를 통해 불러온 데이터의 수정, 삭제 기능을 지원해야할 경우, 이를 구현하는데 상당한 어려움이 존재.
2. Result 로 response 를 감싸 에러를 처리하려고 할 때, PagingData 라는 특수한 타입으로 래핑되어 내려오기 때문에 다른 API 들과 다른 처리 방식이 필요
3. pagination 은 UI와 밀접하게 관련된 동작 처리 임에도 불구하고, 이를 구현하기 위해선, data, domain, presentation 모든 레이어에 paging 관련 의존성을 추가해야함

조사를 해본 결과, 많은 개발자분들이 클린 아키텍처를 적용할 경우 paging3 라이브러리에 대한 부정적인 의견을 가지고 있음을 알 수 있었음
1. [questions_of_jetpack_paging_3](https://www.reddit.com/r/androiddev/comments/1c8qj7l/questions_of_jetpack_paging_3/)
2. [jetpack_paging_v3_vs_clean_architecture](https://www.reddit.com/r/androiddev/comments/1g2lflt/jetpack_paging_v3_vs_clean_architecture/)

LazyColumn 을 이용하면, Pagination 기능을 50줄 정도의 코드로 어렵지 않게 구현할 수 있기 때문에, 별도의 라이브러리를 사용하지 않고, 직접 구현하는 방식을 도입 

```kotlin
// 기기에서 평균적으로 한 화면에 보이는 아이템 개수
private const val LIMIT_COUNT = 4

@Composable
fun InfinityLazyColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    loadMoreLimitCount: Int = LIMIT_COUNT,
    loadMore: () -> Unit = {},
    content: LazyListScope.() -> Unit,
) {
    state.onLoadMore(limitCount = loadMoreLimitCount, action = loadMore)

    LazyColumn(
        modifier = modifier,
        state = state,
        contentPadding = contentPadding,
        reverseLayout = reverseLayout,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
        content = content,
    )
}

@SuppressLint("ComposableNaming")
@Composable
private fun LazyListState.onLoadMore(
    limitCount: Int = LIMIT_COUNT,
    loadOnBottom: Boolean = true,
    action: () -> Unit,
) {
    val reached by remember {
        derivedStateOf {
            reachedBottom(limitCount = limitCount, triggerOnEnd = loadOnBottom)
        }
    }

    LaunchedEffect(reached) {
        if (reached && layoutInfo.totalItemsCount > limitCount) action()
    }
}

/**
 * @param limitCount: 몇 개의 아이템이 남았을 때 트리거 될 지에 대한 정보
 * @param triggerOnEnd: 바닥에 닿았을 때에도 트리거 할 지 여부
 *
 * @return 바닥에 닿았는지 여부(트리거 조건)
 */
private fun LazyListState.reachedBottom(
    limitCount: Int = LIMIT_COUNT,
    triggerOnEnd: Boolean = false,
): Boolean {
    val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
    return (triggerOnEnd && lastVisibleItem?.index == layoutInfo.totalItemsCount - 1) || lastVisibleItem?.index != 0 && lastVisibleItem?.index == layoutInfo.totalItemsCount - (limitCount + 1)
}
```

</details>


## Required

- IDE : Android Studio Koala
- JDK : Java 17을 실행할 수 있는 JDK
- Kotlin Language : 2.2.0

### Language

- Kotlin

### Tech stack & Libraries

- AndroidX
  - Activity Compose
  - Lifecycle & ViewModel Compose
  - Navigation
  - StartUp
  - Splash
  - Room

- Kotlin
  - Coroutine,
  - [Serialization](https://github.com/Kotlin/kotlinx.serialization),
  - [Immutable Collections](https://github.com/Kotlin/kotlinx.collections.immutable)
- Compose
  - Material3
  - Navigation

- Dagger Hilt
- Retrofit, OkHttp
- Logger
- [Compose-Stable-Marker](https://github.com/skydoves/compose-stable-marker)
- [Compose-Extensions](https://github.com/taehwandev/ComposeExtensions)
- Coil, [Landscapist](https://github.com/skydoves/landscapist) 

#### Code analysis

- Ktlint
- Detekt

#### Gradle Dependency

- Gradle Version Catalog

## Architecture

- Clean Architecture
- MVI

## Module
### 앱 모듈 구조

<img width="800" alt="image" src="https://github.com/user-attachments/assets/f52d0b47-adb0-443d-8af8-ee71502bb269">

## Package Structure
```
├── app
│   └── application
├── build-logic
├── core
│   ├── common
│   ├── data
│   ├── database
│   ├── designsystem
│   ├── domain
│   ├── navigation
│   ├── network
│   └── ui
├── feature
│   ├── detail
│   ├── favorites
│   ├── main
│   └── search
└── gradle
    └── libs.versions.toml
```
<br/>
