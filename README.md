# Base Project - Jetpack Compose

Một base project Android sử dụng Jetpack Compose với kiến trúc MVVM, Dependency Injection (Hilt), Compose Destinations và các best practices.

## 🏗️ Kiến trúc

```
app/src/main/java/com/basecompose/baseproject/
├── core/                           # Core components
│   ├── di/                        # Dependency Injection
│   ├── network/                   # Network layer (Retrofit)
│   ├── repository/                # Base repository
│   ├── result/                    # Result wrapper
│   ├── ui/                        # Common UI components
│   │   ├── components/           # Reusable UI components
│   │   └── navigation/           # Navigation setup (Compose Destinations)
│   ├── utils/                     # Utilities & Extensions
│   ├── viewmodel/                 # Base ViewModel
│   └── Application.kt            # Application class
├── features/                      # Feature modules
│   ├── home/                     # Home feature
│   ├── detail/                   # Detail feature
│   └── settings/                 # Settings feature
└── ui/                           # Theme & styling
    └── theme/
```

## 🚀 Tính năng

### Core Components
- **BaseViewModel**: ViewModel cơ sở với state management
- **BaseRepository**: Repository pattern với error handling
- **Result<T>**: Sealed class để xử lý kết quả API
- **NetworkModule**: Retrofit setup với Hilt
- **Common UI Components**: Loading, Error, ResultHandler
- **Compose Destinations**: Type-safe navigation

### Dependencies
- **Jetpack Compose**: UI framework
- **Hilt**: Dependency Injection
- **Retrofit**: Network calls
- **Coroutines**: Async programming
- **Compose Destinations**: Type-safe navigation
- **Material3**: Design system

## 📦 Cài đặt

1. Clone repository
```bash
git clone https://github.com/yukinpb/BaseAndroidJetpack.git
```

2. Mở trong Android Studio
3. Sync Gradle
4. Chạy ứng dụng

## 🔧 Cấu hình

### 1. Thay đổi Base URL
```kotlin
// app/src/main/java/com/basecompose/baseproject/core/network/NetworkModule.kt
.baseUrl("https://your-api-url.com/")
```

### 2. Thêm API Service
```kotlin
// Tạo interface mới trong core/network/
interface YourApiService {
    @GET("endpoint")
    suspend fun getData(): Response<YourData>
}
```

### 3. Tạo Repository
```kotlin
class YourRepository @Inject constructor(
    private val apiService: YourApiService
) : BaseRepository() {
    
    suspend fun getData(): Result<YourData> {
        return safeApiCall {
            apiService.getData()
        }
    }
}
```

### 4. Tạo ViewModel
```kotlin
@HiltViewModel
class YourViewModel @Inject constructor(
    private val repository: YourRepository
) : BaseViewModel() {
    
    private val _uiState = createStateFlow<YourData>(Result.Loading)
    val uiState: StateFlow<Result<YourData>> = _uiState.asReadOnlyStateFlow()
    
    fun loadData() {
        launchWithState(_uiState) {
            repository.getData()
        }
    }
}
```

### 5. Tạo Destination
```kotlin
// core/ui/navigation/Destinations.kt
@Destination
data class YourScreenDestination(
    val id: String,
    val navigator: DestinationsNavigator
)
```

### 6. Tạo Screen
```kotlin
@Composable
fun YourScreen(
    destination: YourScreenDestination,
    viewModel: YourViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    ResultHandler(
        result = uiState,
        onRetry = { viewModel.loadData() },
        content = { data ->
            // Your UI content
        }
    )
}
```

## 🎨 UI Components

### ResultHandler
```kotlin
ResultHandler(
    result = uiState,
    onRetry = { viewModel.loadData() },
    content = { data ->
        // Success content
    }
)
```

### LoadingComponent
```kotlin
LoadingComponent(
    modifier = Modifier.fillMaxSize()
)
```

### ErrorComponent
```kotlin
ErrorComponent(
    message = "Error message",
    onRetry = { /* retry logic */ }
)
```

## 📱 Navigation với Compose Destinations

### Tạo Destination mới
```kotlin
// 1. Thêm destination trong Destinations.kt
@Destination
data class NewScreenDestination(
    val parameter: String,
    val navigator: DestinationsNavigator
)

// 2. Tạo screen
@Composable
fun NewScreen(destination: NewScreenDestination) {
    // Your screen content
}

// 3. Navigate
destination.navigator.navigate(NewScreenDestination("param", destination.navigator))
```

### Navigation với Parameters
```kotlin
// Destination với parameters
@Destination(
    navArgsDelegate = DetailArgs::class
)
data class DetailDestination(
    val id: String,
    val title: String,
    val navigator: DestinationsNavigator
)

data class DetailArgs(
    val id: String,
    val title: String
)

// Navigate với parameters
destination.navigator.navigate(
    DetailDestination(
        id = "123",
        title = "Sample Title",
        navigator = destination.navigator
    )
)
```

### Navigation Utils
```kotlin
// Clear back stack
destination.navigator.navigateAndClearBackStack(HomeDestination(destination.navigator))

// Navigate với singleTop
destination.navigator.navigateSingleTop(YourDestination(destination.navigator))

// Navigate với popUpTo
destination.navigator.navigateAndPopUpTo(
    destination = YourDestination(destination.navigator),
    popUpTo = HomeDestination(destination.navigator),
    inclusive = true
)
```

## 🔄 State Management

### ViewModel State
```kotlin
private val _uiState = createStateFlow<Data>(Result.Loading)
val uiState: StateFlow<Result<Data>> = _uiState.asReadOnlyStateFlow()
```

### Collect State
```kotlin
val uiState by viewModel.uiState.collectAsState()
```

## 🛠️ Utilities

### Extensions
```kotlin
// UI Extensions
Modifier.defaultPadding()
Modifier.smallPadding()

// String Extensions
nullableString.orEmpty()

// Navigation Extensions
navigator.navigateAndClearBackStack(destination)
navigator.navigateSingleTop(destination)
```

### Constants
```kotlin
Constants.BASE_URL
Constants.DEFAULT_PADDING
Constants.NETWORK_ERROR
```

## 📋 Checklist cho dự án mới

- [ ] Thay đổi package name
- [ ] Cập nhật applicationId trong build.gradle
- [ ] Thay đổi BASE_URL trong NetworkModule
- [ ] Cập nhật app name và icon
- [ ] Thêm các API services cần thiết
- [ ] Tạo các repositories
- [ ] Tạo các ViewModels
- [ ] Tạo các destinations và screens
- [ ] Test các components

## 🎯 Lợi ích của Compose Destinations

- **Type-safe**: Compile-time safety cho navigation
- **Auto-generated**: Tự động generate navigation code
- **Parameter passing**: Type-safe parameter passing
- **Deep linking**: Hỗ trợ deep linking
- **Bottom sheets**: Hỗ trợ bottom sheet navigation
- **Nested navigation**: Hỗ trợ nested navigation graphs

## 🤝 Contributing

1. Fork project
2. Tạo feature branch
3. Commit changes
4. Push to branch
5. Tạo Pull Request

## 📄 License

MIT License

## 🔗 Links

- [Repository](https://github.com/yukinpb/BaseAndroidJetpack)
- [Compose Destinations](https://github.com/raamcosta/compose-destinations)
- [Material3](https://m3.material.io/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose) 