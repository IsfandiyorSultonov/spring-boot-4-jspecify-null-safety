# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 4.0.1 learning project demonstrating JSpecify null safety annotations with Error Prone static analysis. The project showcases how to use `@NullMarked` and `@Nullable` annotations to declare null contracts at compile time, with static analysis validation via Error Prone and NullAway.

**Key Technologies:**
- Spring Boot 4.0.1
- Java 25
- Gradle
- Error Prone 2.37.0 with NullAway 0.12.6
- JSpecify annotations

## Build and Development Commands

### Build the project
```bash
./gradlew build
```

### Run the application
```bash
./gradlew bootRun
```

### Run tests
```bash
./gradlew test
```

### Run a single test
```bash
./gradlew test --tests JspecifyNullSafetyApplicationTests
```

### Compile only (useful for checking Error Prone analysis)
```bash
./gradlew compileJava
```

### Clean build
```bash
./gradlew clean build
```

## Project Architecture

### Null Safety Configuration

The project uses a package-level `@NullMarked` annotation in `users/package-info.java` which declares that all parameters and return values in that package must be non-null by default. This is enforced at compile time through Error Prone's NullAway checker.

**Configuration in build.gradle:**
```gradle
options.errorprone {
    disableAllChecks = true                              // Focus only on null safety
    option("NullAway:OnlyNullMarked", "true")           // Check @NullMarked packages/classes only
    option("NullAway:JSpecifyMode", "true")             // Enable JSpecify standard support
    option("NullAway:CustomContractAnnotations", "org.springframework.lang.Contract")  // Support Spring contracts
    error("NullAway")                                    // Treat violations as build errors
}
```

⚠️ **Important Configuration Notes:**
- Do NOT combine `OnlyNullMarked` with `AnnotatedPackages` - they are mutually exclusive
- `JSpecifyMode=true` enables proper handling of `@Nullable` on arrays, generics, and complex types
- `CustomContractAnnotations` allows Spring Framework's `@Contract` annotation for additional null safety semantics

### Code Structure

```
src/main/java/org/learn/spring/boot/
├── JspecifyNullSafetyApplication.java        # Spring Boot entry point
├── users/                                    # @NullMarked at package level
│   ├── package-info.java                    # Package-level null safety declaration
│   ├── User.java                            # Record model - non-null fields
│   ├── UserService.java                     # Service with @Nullable return
│   ├── UserController.java                  # REST controller with null checks
│   └── ServiceLayerNullMarked.java           # Class-level @NullMarked example
├── orders/                                   # @NullMarked at package level
│   ├── package-info.java                    # Package-level null safety declaration
│   ├── Order.java                           # DTO with non-null fields
│   ├── OrderService.java                    # Service with @Nullable parameter
│   └── OrderController.java                 # REST controller
├── menu/                                     # No package-level marking (advanced null examples)
│   ├── MenuService.java                     # Nullable arrays, nullable elements in arrays
│   ├── CoffeeRequest.java
│   ├── MenuController.java
│   └── package-info.java
└── reviews/                                  # @NullMarked at package level
    ├── package-info.java                    # Package-level null safety declaration
    ├── ReviewsService.java                  # Collections with nullable elements
    └── ReviewController.java
```

### Design Patterns Demonstrated

#### 1. @Nullable Return Types
The `UserService.findByEmail()` method demonstrates nullable returns:
```java
@Nullable User findByEmail(String email) {
    return users.stream().findFirst().orElse(null);  // Can return null
}
```
Callers must check for null before dereferencing.

#### 2. @Nullable Parameters
The `OrderService.createOrder()` method shows optional parameters:
```java
public Order createOrder(String email, @Nullable String promoCode) {
    // email is required (non-null)
    sendEmailOrderConfirmation(email);

    // promo code is optional (can be null)
    if (promoCode != null) {
        applyPromoCode(promoCode);
    }
}
```

#### 3. Class-Level @NullMarked
The `ServiceLayerNullMarked` class demonstrates null safety at the class level:
```java
@Service
@NullMarked
public class ServiceLayerNullMarked {
    public @Nullable User getUserByEmail(String email) {
        // class requires non-null parameters but allows nullable returns
    }
}
```

#### 4. Collections with Nullable Elements
The `ReviewsService` shows how to mark collection elements as nullable:
```java
public List<@Nullable String> getReviewMessage() {
    List<@Nullable String> reviews = new ArrayList<>();
    reviews.add("review");
    reviews.add(null);  // Allowed - elements can be null
    return reviews;
}
```

#### 5. Complex Null Marking on Arrays
The `MenuService` demonstrates different array nullability patterns:
```java
// Nullable array itself
public @Nullable String[] dailySpecials()

// Nullable array elements
public String @Nullable[] dailySpecialsElementNullable()

// Both nullable
public @Nullable String @Nullable[] dailySpecialsArrayElementNullable()
```

**Important:** Callers of nullable-returning methods must handle the null case explicitly using null checks, Optional patterns, or other appropriate defensive code.

### REST APIs

Multiple controllers demonstrate null safety in different scenarios:
- **Users Module**: `GET /users/get-user-by-email?email={email}` - Returns User (may be null)
- **Orders Module**: `POST /orders/create` - Creates order with optional promo code parameter
- **Menu Module**: `GET /menu/daily-specials` - Returns nullable arrays and collections
- **Reviews Module**: `GET /reviews/messages` - Returns list with nullable elements

## Important Notes

### Core Principles
- **Null safety is enforced at compile time** - NullAway violations prevent the build from succeeding
- **@NullMarked can apply at package or class level** - All methods/parameters are non-null unless explicitly marked `@Nullable`
- **Error Prone analysis is strict** - The build.gradle treats all NullAway violations as build errors, not warnings
- **JSpecifyMode enables advanced null marking** - Allows precise control over nullability of arrays, generics, and collection elements

### Development Practices
- **Handle @Nullable returns carefully** - When a method returns `@Nullable`, callers must check for null before dereferencing
- **Use @Nullable sparingly** - Default to non-null contracts; only use `@Nullable` when necessary
- **Position of @Nullable matters** - In arrays: `@Nullable String[]` (array is null) vs `String @Nullable[]` (elements are null)
- **Null checks are IDE-aware** - IntelliJ and other IDEs integrate with NullAway to show errors as you type
- **Build will fail on null violations** - This is a feature, not a bug—it prevents deployment of unsafe code

## Known Issues and Fixes

### Null Dereference in UserController (UserController.java:20)

The `getUserByEmail()` method calls `user.firstname()` without checking if `user` is null first. Since `UserService.findByEmail()` returns `@Nullable User`, this causes a NullAway compilation error: "dereferenced expression user is @Nullable".

**Fix:** Add a null check:
```java
if (user != null && user.firstname().equals("dev")) {
    // handle developer case
}
```

Or use Optional pattern:
```java
Optional.ofNullable(userService.findByEmail(email))
    .filter(u -> u.firstname().equals("dev"))
    .ifPresent(u -> IO.println("Developer detected"));
```

Refer to README.md for complete details on all configuration and code issues.

## Documentation and Resources

- **Spring Framework Null-Safety Guide**: https://docs.spring.io/spring-framework/reference/core/null-safety.html
  - Official Spring documentation on null safety annotations and integration

- **Spring Boot 4 Null Safety**: https://www.danvega.dev/blog/spring-boot-4-null-safety
  - Practical guide and examples for implementing null safety in Spring Boot 4

- **NullAway Documentation**: https://github.com/uber/NullAway/wiki
  - Complete NullAway configuration and troubleshooting guide

- **JSpecify Standard**: https://jspecify.dev
  - JSpecify standard for null-safety annotations in Java