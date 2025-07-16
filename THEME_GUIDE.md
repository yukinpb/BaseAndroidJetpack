# Theme Guide - Base Project

## 🎨 Material3 Theme System

Base project sử dụng Material3 Design System với theme chuẩn, hỗ trợ light/dark mode và dynamic colors.

## 🎯 Tính năng Theme

### Color System
- **Light Theme**: Màu sắc tươi sáng, dễ đọc
- **Dark Theme**: Màu sắc tối, tiết kiệm pin
- **Dynamic Colors**: Tự động thích ứng với wallpaper (Android 12+)

### Typography
- **Material3 Type Scale**: Typography chuẩn theo Material Design
- **Responsive**: Tự động điều chỉnh theo kích thước màn hình
- **Accessibility**: Hỗ trợ accessibility features

### Components
- **Themed Components**: Tất cả components đều sử dụng theme
- **Consistent**: Nhất quán trong toàn bộ app
- **Customizable**: Dễ dàng tùy chỉnh

## 🚀 Cách sử dụng

### 1. Sử dụng Themed Components

```kotlin
// Themed Text
ThemedText(
    text = "Hello World",
    style = MaterialTheme.typography.headlineMedium
)

// Themed Button
ThemedButton(onClick = { /* action */ }) {
    ThemedText("Click me")
}

// Themed Card
ThemedCard {
    ThemedText("Card content")
}
```

### 2. Truy cập Theme Colors

```kotlin
// Lấy color scheme
val colors = MaterialTheme.colorScheme

// Sử dụng colors
Text(
    text = "Sample text",
    color = colors.primary
)
```

### 3. Sử dụng Typography

```kotlin
// Các style có sẵn
MaterialTheme.typography.headlineLarge
MaterialTheme.typography.headlineMedium
MaterialTheme.typography.headlineSmall
MaterialTheme.typography.titleLarge
MaterialTheme.typography.titleMedium
MaterialTheme.typography.titleSmall
MaterialTheme.typography.bodyLarge
MaterialTheme.typography.bodyMedium
MaterialTheme.typography.bodySmall
MaterialTheme.typography.labelLarge
MaterialTheme.typography.labelMedium
MaterialTheme.typography.labelSmall
```

### 4. Check Dark/Light Theme

```kotlin
@Composable
fun MyComponent() {
    val isDark = isDarkTheme()
    
    ThemedText(
        text = if (isDark) "Dark Mode" else "Light Mode",
        color = if (isDark) Color.White else Color.Black
    )
}
```

## 🎨 Themed Components

### Text Components
```kotlin
ThemedText(
    text = "Sample text",
    style = MaterialTheme.typography.bodyMedium,
    color = MaterialTheme.colorScheme.onSurface
)
```

### Button Components
```kotlin
// Primary Button
ThemedButton(onClick = { /* action */ }) {
    ThemedText("Primary Button")
}

// Outlined Button
ThemedOutlinedButton(onClick = { /* action */ }) {
    ThemedText("Outlined Button")
}

// Text Button
ThemedTextButton(onClick = { /* action */ }) {
    ThemedText("Text Button")
}
```

### Card Components
```kotlin
// Regular Card
ThemedCard {
    ThemedText("Card content")
}

// Elevated Card
ThemedElevatedCard {
    ThemedText("Elevated card content")
}

// Outlined Card
ThemedOutlinedCard {
    ThemedText("Outlined card content")
}
```

### Loading & Error Components
```kotlin
// Loading
ThemedLoading()

// Error
ThemedError(
    message = "Something went wrong",
    onRetry = { /* retry logic */ }
)
```

## 🌈 Color Palette

### Primary Colors
- `primary`: Màu chính của app
- `onPrimary`: Màu text trên primary
- `primaryContainer`: Container cho primary
- `onPrimaryContainer`: Text trên primary container

### Secondary Colors
- `secondary`: Màu phụ
- `onSecondary`: Text trên secondary
- `secondaryContainer`: Container cho secondary
- `onSecondaryContainer`: Text trên secondary container

### Surface Colors
- `surface`: Màu bề mặt
- `onSurface`: Text trên surface
- `surfaceVariant`: Biến thể của surface
- `onSurfaceVariant`: Text trên surface variant

### Error Colors
- `error`: Màu lỗi
- `onError`: Text trên error
- `errorContainer`: Container cho error
- `onErrorContainer`: Text trên error container

## 📱 Responsive Design

### Typography Scale
```kotlin
// Large screens
MaterialTheme.typography.headlineLarge // 57sp

// Medium screens
MaterialTheme.typography.headlineMedium // 45sp

// Small screens
MaterialTheme.typography.headlineSmall // 36sp
```

### Spacing
```kotlin
// Standard spacing
Spacer(modifier = Modifier.height(16.dp))
Spacer(modifier = Modifier.width(8.dp))

// Custom spacing
Modifier.padding(16.dp)
Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
```

## 🎯 Best Practices

### 1. Sử dụng Themed Components
```kotlin
// ✅ Good
ThemedText("Hello")
ThemedButton(onClick = {}) { ThemedText("Click") }

// ❌ Avoid
Text("Hello")
Button(onClick = {}) { Text("Click") }
```

### 2. Sử dụng Material3 Colors
```kotlin
// ✅ Good
color = MaterialTheme.colorScheme.primary

// ❌ Avoid
color = Color(0xFF6750A4)
```

### 3. Sử dụng Typography Scale
```kotlin
// ✅ Good
style = MaterialTheme.typography.headlineMedium

// ❌ Avoid
fontSize = 24.sp
```

### 4. Check Theme Mode
```kotlin
@Composable
fun AdaptiveComponent() {
    val isDark = isDarkTheme()
    
    ThemedText(
        text = "Adaptive text",
        color = if (isDark) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onBackground
        }
    )
}
```

## 🔧 Customization

### 1. Thay đổi Primary Color
```kotlin
// Trong Color.kt
val md_theme_light_primary = Color(0xFFYOUR_COLOR)
val md_theme_dark_primary = Color(0xFFYOUR_DARK_COLOR)
```

### 2. Thêm Custom Colors
```kotlin
// Trong Color.kt
val customColor = Color(0xFFYOUR_COLOR)

// Trong ColorScheme
val LightColorScheme = lightColorScheme(
    // ... existing colors
    surface = customColor
)
```

### 3. Custom Typography
```kotlin
// Trong Type.kt
val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    )
    // ... other styles
)
```

## 📋 Checklist

- [ ] Sử dụng Themed Components
- [ ] Sử dụng Material3 Colors
- [ ] Sử dụng Typography Scale
- [ ] Test trên Light/Dark mode
- [ ] Test với Dynamic Colors
- [ ] Check Accessibility
- [ ] Responsive Design
- [ ] Consistent Spacing

## 🎨 Theme Examples

### Card Layout
```kotlin
ThemedCard(
    modifier = Modifier.fillMaxWidth()
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        ThemedText(
            text = "Card Title",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        ThemedText(
            text = "Card description",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
```

### Button Group
```kotlin
Row(
    horizontalArrangement = Arrangement.spacedBy(8.dp)
) {
    ThemedButton(onClick = {}) {
        ThemedText("Primary")
    }
    ThemedOutlinedButton(onClick = {}) {
        ThemedText("Secondary")
    }
}
```

### List Item
```kotlin
Row(
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically
) {
    Icon(
        imageVector = Icons.Default.Star,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.width(16.dp))
    Column(modifier = Modifier.weight(1f)) {
        ThemedText(
            text = "List Item",
            style = MaterialTheme.typography.titleMedium
        )
        ThemedText(
            text = "Description",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
``` 