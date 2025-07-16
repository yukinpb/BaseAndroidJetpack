# Template tạo Feature mới với Compose Destinations

## 1. Tạo cấu trúc thư mục
```
features/
└── your_feature/
    ├── data/
    │   ├── model/
    │   ├── repository/
    │   └── datasource/
    ├── domain/
    │   ├── model/
    │   ├── repository/
    │   └── usecase/
    └── presentation/
        ├── screen/
        ├── viewmodel/
        └── state/
```

## 2. Tạo Data Model
```kotlin
// features/your_feature/data/model/YourDataModel.kt
data class YourDataModel(
    val id: String,
    val title: String,
    val description: String
)
```

## 3. Tạo API Service
```kotlin
// core/network/YourApiService.kt
interface YourApiService {
    @GET("your-endpoint")
    suspend fun getYourData(): Response<List<YourDataModel>>
}
```

## 4. Tạo Repository
```kotlin
// features/your_feature/data/repository/YourRepositoryImpl.kt
class YourRepositoryImpl @Inject constructor(
    private val apiService: YourApiService
) : BaseRepository(), YourRepository {
    
    override suspend fun getYourData(): Result<List<YourDataModel>> {
        return safeApiCall {
            apiService.getYourData()
        }
    }
}
```

## 5. Tạo Domain Repository Interface
```kotlin
// features/your_feature/domain/repository/YourRepository.kt
interface YourRepository {
    suspend fun getYourData(): Result<List<YourDataModel>>
}
```

## 6. Tạo Use Case
```kotlin
// features/your_feature/domain/usecase/GetYourDataUseCase.kt
class GetYourDataUseCase @Inject constructor(
    private val repository: YourRepository
) {
    suspend operator fun invoke(): Result<List<YourDataModel>> {
        return repository.getYourData()
    }
}
```

## 7. Tạo State
```kotlin
// features/your_feature/presentation/state/YourScreenState.kt
data class YourScreenState(
    val isLoading: Boolean = false,
    val data: List<YourDataModel> = emptyList(),
    val error: String? = null
)
```

## 8. Tạo ViewModel
```kotlin
// features/your_feature/presentation/viewmodel/YourViewModel.kt
@HiltViewModel
class YourViewModel @Inject constructor(
    private val getYourDataUseCase: GetYourDataUseCase
) : BaseViewModel() {
    
    private val _state = createStateFlow(YourScreenState())
    val state: StateFlow<YourScreenState> = _state.asReadOnlyStateFlow()
    
    init {
        loadData()
    }
    
    fun loadData() {
        launchWithState(_state) {
            val result = getYourDataUseCase()
            YourScreenState(
                isLoading = false,
                data = result.data ?: emptyList(),
                error = result.exception?.message
            )
        }
    }
}
```

## 9. Tạo Destination
```kotlin
// core/ui/navigation/Destinations.kt
@Destination
data class YourFeatureDestination(
    val navigator: DestinationsNavigator
)

// Hoặc với parameters
@Destination(
    navArgsDelegate = YourFeatureArgs::class
)
data class YourFeatureDestination(
    val id: String,
    val title: String,
    val navigator: DestinationsNavigator
)

data class YourFeatureArgs(
    val id: String,
    val title: String
)
```

## 10. Tạo Screen
```kotlin
// features/your_feature/presentation/screen/YourScreen.kt
@Composable
fun YourScreen(
    destination: YourFeatureDestination,
    viewModel: YourViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Feature") },
                navigationIcon = {
                    IconButton(onClick = { destination.navigator.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    LoadingComponent()
                }
                state.error != null -> {
                    ErrorComponent(
                        message = state.error!!,
                        onRetry = { viewModel.loadData() }
                    )
                }
                else -> {
                    LazyColumn {
                        items(state.data) { item ->
                            YourDataItem(item = item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun YourDataItem(item: YourDataModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
```

## 11. Navigate từ Screen khác
```kotlin
// Trong screen khác
Button(
    onClick = {
        destination.navigator.navigate(
            YourFeatureDestination(
                navigator = destination.navigator
            )
        )
    }
) {
    Text("Go to Your Feature")
}

// Hoặc với parameters
Button(
    onClick = {
        destination.navigator.navigate(
            YourFeatureDestination(
                id = "123",
                title = "Sample Title",
                navigator = destination.navigator
            )
        )
    }
) {
    Text("Go to Your Feature with Params")
}
```

## 12. Thêm Dependencies trong Hilt Module
```kotlin
// features/your_feature/di/YourFeatureModule.kt
@Module
@InstallIn(SingletonComponent::class)
object YourFeatureModule {
    
    @Provides
    @Singleton
    fun provideYourRepository(
        apiService: YourApiService
    ): YourRepository {
        return YourRepositoryImpl(apiService)
    }
    
    @Provides
    @Singleton
    fun provideGetYourDataUseCase(
        repository: YourRepository
    ): GetYourDataUseCase {
        return GetYourDataUseCase(repository)
    }
}
```

## 13. Navigation Utils
```kotlin
// Sử dụng các extension functions
destination.navigator.navigateAndClearBackStack(YourFeatureDestination(destination.navigator))
destination.navigator.navigateSingleTop(YourFeatureDestination(destination.navigator))
destination.navigator.navigateWithRestoreState(YourFeatureDestination(destination.navigator))
```

## Checklist hoàn thành Feature:
- [ ] Tạo cấu trúc thư mục
- [ ] Tạo Data Model
- [ ] Tạo API Service
- [ ] Tạo Repository Implementation
- [ ] Tạo Domain Repository Interface
- [ ] Tạo Use Case
- [ ] Tạo State
- [ ] Tạo ViewModel
- [ ] Tạo Destination
- [ ] Tạo Screen
- [ ] Thêm Navigation logic
- [ ] Thêm Dependencies
- [ ] Test Feature 