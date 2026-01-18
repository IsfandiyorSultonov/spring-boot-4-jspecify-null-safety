# spring-boot-4-jspecify-null-safety

A Spring Boot 4.0.1 learning project demonstrating JSpecify null safety annotations with Error Prone static analysis.

## Overview

This project showcases how to use JSpecify's `@NullMarked` and `@Nullable` annotations to declare null contracts at compile time. The project uses Error Prone with NullAway checker to enforce null safety rules during compilation, catching potential null pointer dereferences before runtime.

**Key Technologies:**
- Spring Boot 4.0.1
- Java 25
- Gradle
- Error Prone 2.37.0
- NullAway 0.12.6
- JSpecify annotations

## Documentation References

### Official Spring Framework Documentation
- **Spring Framework Null-Safety**: [https://docs.spring.io/spring-framework/reference/core/null-safety.html](https://docs.spring.io/spring-framework/reference/core/null-safety.html)
  - Comprehensive guide on Spring's approach to null safety and how it integrates with JSpecify

### Additional Resources
- **Spring Boot 4 Null Safety**: [https://www.danvega.dev/blog/spring-boot-4-null-safety](https://www.danvega.dev/blog/spring-boot-4-null-safety)
  - Practical guide and examples for implementing null safety in Spring Boot 4

## Build and Run

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

### Compile only (to check Error Prone analysis)
```bash
./gradlew compileJava
```

### Clean build
```bash
./gradlew clean build
```

## How NullAway Validates Null Safety at Compile Time

NullAway is an Error Prone plugin that performs **static analysis** at compile time to detect potential null pointer dereferences before your code ever runs. Here's how it works:

### Compile-Time Validation Process

1. **Declaration Phase**: You mark packages or classes with `@NullMarked` to declare null contracts
2. **Annotation Scanning**: NullAway scans your code during compilation to find all `@NullMarked` and `@Nullable` annotations
3. **Type Checking**: For every method call, assignment, and dereference, NullAway checks if:
   - A non-null value is being assigned to a variable that accepts null
   - A value marked as `@Nullable` is being used without a null check
   - Parameters are being passed without respecting null contracts
4. **Error Reporting**: If a violation is found, the build fails with a compiler error
5. **Prevention**: The code cannot compile until all null safety violations are fixed

### Why Compile-Time Validation Matters

- **Bugs Caught Early**: Null pointer exceptions are detected before deployment
- **No Runtime Overhead**: Unlike runtime checks, this adds zero performance cost
- **Developer Feedback**: IDE integration shows errors as you type
- **Build Confidence**: A successful build guarantees null safety compliance

## NullAway Configuration Details

The project configures NullAway in `build.gradle` with the following options:

```gradle
tasks.withType(JavaCompile).configureEach {
    options.errorprone {
        disableAllChecks = true                              // Disable all Error Prone checks except those explicitly enabled
        option("NullAway:OnlyNullMarked", "true")           // Only check packages/classes marked with @NullMarked
        option("NullAway:JSpecifyMode", "true")             // Enable JSpecify standard for null checking
        option("NullAway:CustomContractAnnotations", "org.springframework.lang.Contract")  // Support Spring's @Contract annotation
        error("NullAway")                                    // Treat NullAway violations as build errors (not warnings)
    }
}
```

### Configuration Options Explained

| Option | Value | Purpose |
|--------|-------|---------|
| `OnlyNullMarked` | `true` | Only perform null safety checks on code in packages/classes explicitly marked with `@NullMarked`. Unmarked code is ignored. |
| `JSpecifyMode` | `true` | Enable support for JSpecify standard annotations, including proper handling of `@Nullable` on arrays, generics, and complex types. |
| `CustomContractAnnotations` | `org.springframework.lang.Contract` | Recognize Spring Framework's `@Contract` annotation for null safety contracts (e.g., contract="_ -> new"). |
| `disableAllChecks` | `true` | Disable all other Error Prone checks, focusing only on null safety. |
| `error("NullAway")` | — | Treat any NullAway violation as a compilation error, preventing builds with null safety violations. |

### Important: Mutually Exclusive Configuration

⚠️ **Do NOT combine `OnlyNullMarked` with `AnnotatedPackages`** - they are mutually exclusive:
- Use `OnlyNullMarked=true` to check only `@NullMarked` annotated code (recommended for projects starting out)
- Use `AnnotatedPackages` to explicitly list packages to check (alternative approach)

## Project Architecture

### Two Approaches to Null Marking

This project demonstrates both approaches to declaring null safety:

#### 1. Package-Level Null Marking (`@NullMarked` in package-info.java)

The `users/`, `orders/`, `menu/`, and `reviews/` packages apply `@NullMarked` at the package level:

```java
@NullMarked
package org.learn.spring.boot.users;

import org.jspecify.annotations.NullMarked;
```

**Effect:** All methods, parameters, and return types in the package are **non-null by default** unless explicitly marked with `@Nullable`.

#### 2. Class-Level Null Marking (`@NullMarked` on class)

The `ServiceLayerNullMarked` class applies `@NullMarked` at the class level:

```java
@Service
@NullMarked  // Applies to this class only
public class ServiceLayerNullMarked {
    public @Nullable User getUserByEmail(String email) {
        // email is non-null (required)
        // return type is nullable (can return null)
    }
}
```

**Effect:** Only that specific class has null safety enforcement.

### Code Structure

```
src/main/java/org/learn/spring/boot/
├── JspecifyNullSafetyApplication.java        # Spring Boot entry point
├── users/                                    # @NullMarked at package level
│   ├── package-info.java                    # Package-level null safety declaration
│   ├── User.java                            # Record model - all fields non-null
│   ├── UserService.java                     # Service with @Nullable return
│   ├── UserController.java                  # REST controller with null checks
│   └── ServiceLayerNullMarked.java           # Class-level @NullMarked example
├── orders/                                   # @NullMarked at package level
│   ├── package-info.java                    # Package-level null safety declaration
│   ├── Order.java                           # Record with @Nullable promoCode field
│   ├── OrderService.java                    # Service with @Nullable parameter
│   └── OrderController.java                 # REST controller with null safety checks
├── menu/                                     # @NullMarked at package level
│   ├── package-info.java                    # Package-level null safety declaration
│   ├── CoffeeRequest.java                   # Record with mixed nullable fields and validation
│   ├── MenuService.java                     # Various array null marking patterns
│   └── MenuController.java                  # Returns nullable arrays
└── reviews/                                  # @NullMarked at package level
    ├── package-info.java                    # Package-level null safety declaration
    ├── ReviewsService.java                  # Collections with nullable elements
    └── ReviewController.java                # REST endpoint returning List with nullable elements
```

## Real-World Code Examples from the Project

### 1. Record with Optional Field

**Order.java** - Demonstrates nullable field in a record:
```java
public record Order(
    String email,                   // non-null field
    @Nullable String promoCode      // nullable field
) {
}
```

**Usage in OrderController.java**:
```java
if (order.promoCode() != null && order.promoCode().equals(promoCode)) {
    IO.println("Order created");
}
```

### 2. Record with Validation

**CoffeeRequest.java** - Validates non-null fields:
```java
public record CoffeeRequest(
    String coffeeType,              // non-null
    Long quantityOfBeen,            // non-null
    @Nullable Long quantityOfMilk   // can be null
) {
    public CoffeeRequest {
        if (quantityOfBeen == null) {
            throw new IllegalArgumentException("quantityOfBeen is null");
        }
        if (coffeeType == null) {
            throw new IllegalArgumentException("coffeeType is null");
        }
    }
}
```

### 3. Nullable Method Parameters

**OrderService.java** - Optional promo code parameter:
```java
public Order createOrder(String email, @Nullable String promoCode) {
    // email is required (non-null)
    sendEmailOrderConfirmation(email);

    // promo code is optional (can be null)
    if (promoCode != null) {
        applyPromoCode(promoCode);
    }

    return new Order(email, promoCode);
}
```

**OrderController.java** - Request parameter with required/optional:
```java
@PostMapping
public Order createOrder(
    @RequestParam(required = true) String email,        // Required
    @RequestParam(required = false) String promoCode    // Optional
) {
    Order order = orderService.createOrder(email, promoCode);

    // Must check promoCode before using
    if (order.promoCode() != null && order.promoCode().equals(promoCode)) {
        IO.println("Order created");
    }
    return order;
}
```

### 4. Nullable Return Types

**UserService.java** - Returns nullable User:
```java
@Nullable
public User findByEmail(String email) {
    return users.stream()
        .filter(user -> user.email().equalsIgnoreCase(email))
        .findFirst()
        .orElse(null);  // Explicitly returns null
}
```

**UserController.java** - Handles nullable return:
```java
@GetMapping("/get-user-by-email")
public User getUserByEmail(@RequestParam String email) {
    User user = userService.findByEmail(email);  // May return null

    if (user != null && user.firstname().equals("dev")) {  // Null check required
        IO.println("He is Developer from 'OCTO' LLC. He is Spring learner");
    }

    return user;
}
```

### 5. Array Nullability Patterns

**MenuService.java** - Different array null marking:
```java
// @Nullable String[] - array itself can be null
public @Nullable String[] dailySpecials(){
    return new String[]{"milk", "potato", "end"};
}

// String @Nullable[] - array elements can be null
public String @Nullable[] dailySpecialsElementNullable(){
    String @Nullable[] arr = new String[3];
    arr[0] = "milk";
    arr[1] = "potato";
    // arr[2] = null; // NullAway validation won't pass if uncommented
    return arr;
}

// @Nullable String[] - array elements can be null
public @Nullable String[] dailySpecialsArrayNullable(){
    return new String[]{"milk", "potato", null, "end"};  // Elements can be null
}

// @Nullable String @Nullable[] - both array and elements can be null
public @Nullable String @Nullable[] dailySpecialsElementArrayNullable(){
    @Nullable String @Nullable[] arr = new String[3];
    arr[0] = "milk";
    arr[1] = null;  // Elements can be null
    return arr;
}
```

### 6. Collections with Nullable Elements

**ReviewsService.java** - List with nullable elements:
```java
public List<@Nullable String> getReviewMessage() {
    List<@Nullable String> reviews = new ArrayList<>();
    reviews.add("hello world");
    reviews.add(null);           // Allowed - elements can be null
    reviews.add("review world");
    reviews.add(null);
    reviews.add("close world");

    return reviews;
}
```

**ReviewController.java** - Expose nullable list:
```java
@GetMapping
public List<@Nullable String> getReviews() {
    return reviewsService.getReviewMessage();
}
```

### 7. Returning Nullable Arrays

**MenuController.java** - Returns nullable array:
```java
@GetMapping
public @Nullable String[] getDailySpecials(){
    return menuService.dailySpecials();
}
```

## REST APIs

The project demonstrates null safety in several REST endpoints:

| Endpoint | Method | Description | Null Safety |
|----------|--------|-------------|-------------|
| `/users/get-user-by-email` | GET | Get user by email | Returns `@Nullable User`, caller must null check |
| `/orders` | POST | Create order with optional promo code | `promoCode` parameter is `@Nullable` |
| `/menu` | GET | Get daily specials | Returns `@Nullable String[]` array |
| `/reviews` | GET | Get customer reviews | Returns `List<@Nullable String>` with nullable elements |

## Key Concepts

### @NullMarked Annotation

The `@NullMarked` annotation declares that all types within a scope are non-null by default:

```java
// Package-level (applies to entire package)
@NullMarked
package org.learn.spring.boot.users;

// Class-level (applies to single class)
@Service
@NullMarked
public class ServiceLayerNullMarked { }
```

**Effects:**
- All method parameters cannot be null (must pass non-null values)
- All return types cannot be null (methods must return non-null)
- All record/class fields cannot be null (must be initialized)
- Applies recursively to inner classes and nested types

### @Nullable Annotation

The `@Nullable` annotation explicitly marks individual elements as nullable:

```java
// Nullable return type
public @Nullable User findByEmail(String email) {
    return users.stream().findFirst().orElse(null);  // Allowed
}

// Nullable parameter
public void applyDiscount(@Nullable String promoCode) {
    if (promoCode != null) { /* use promoCode */ }
}
```

**Important:** Callers must check for null before dereferencing nullable values.

### Advanced Null Marking: Arrays and Collections

JSpecify provides precise null marking for complex types:

#### Nullable Array vs Nullable Elements

The position of `@Nullable` matters:

```java
// @Nullable String[] - array itself can be null, elements cannot be null
public @Nullable String[] dailySpecials()

// String @Nullable[] - array cannot be null, but elements CAN be null
public String @Nullable[] dailySpecialsElementNullable()

// @Nullable String @Nullable[] - both array and elements can be null
public @Nullable String @Nullable[] dailySpecialsArrayElementNullable()
```

#### Collections with Nullable Elements

```java
// List<@Nullable String> - list is non-null, but elements can be null
@NullMarked
public List<@Nullable String> getReviewMessages() {
    List<@Nullable String> reviews = new ArrayList<>();
    reviews.add("review");
    reviews.add(null);  // Allowed: elements are nullable
    return reviews;     // Non-null list
}
```

**Key Point:** The position of `@Nullable` is crucial:
- Before the container type (e.g., `@Nullable String[]`) = the container itself is nullable
- After the container type (e.g., `String @Nullable[]`) = the elements are nullable

### Error Prone with NullAway

NullAway is an Error Prone plugin that provides compile-time null safety checking:

**Features:**
- Detects potential null pointer dereferences before runtime
- Performs whole-program analysis during compilation
- Configured to treat violations as build errors
- Zero runtime performance overhead
- IDE integration for real-time error feedback

**When NullAway Reports Errors:**
1. Dereferencing a `@Nullable` value without null check
2. Passing a `@Nullable` value to a non-null parameter
3. Returning a `@Nullable` value where non-null is expected
4. Using methods marked `@Nullable` without proper null handling
5. Assigning nullable types to non-nullable variables

## Known Issues and Lessons

### Issue: Dereferencing Nullable Values Without Null Check

**Problem:**
```java
User user = userService.findByEmail(email);  // Returns @Nullable User
if (user.firstname().equals("dev")) {        // ERROR: dereferencing nullable!
    // ...
}
```

**Error Message:**
```
[NullAway] dereferenced expression user is @Nullable
```

**Solution - Null Check:**
```java
if (user != null && user.firstname().equals("dev")) {
    // ...
}
```

**Solution - Optional Pattern:**
```java
Optional.ofNullable(userService.findByEmail(email))
    .filter(u -> u.firstname().equals("dev"))
    .ifPresent(u -> IO.println("Developer detected"));
```

## Learning Outcomes

This project demonstrates:

### Fundamental Concepts
1. ✅ How to set up null safety checking in a Spring Boot 4 application
2. ✅ How to properly configure Error Prone with NullAway and JSpecify
3. ✅ How compile-time null safety validation works (vs runtime checks)
4. ✅ How NullAway detects null pointer dereferences before code runs

### Implementation Patterns
5. ✅ Package-level `@NullMarked` annotations for entire packages
6. ✅ Class-level `@NullMarked` annotations for individual classes
7. ✅ How `@Nullable` annotations override default non-null contracts
8. ✅ Handling nullable return values with null checks and Optional patterns
9. ✅ Nullable fields in records with documentation and validation
10. ✅ Optional method parameters with `@Nullable` annotation

### Advanced Topics
11. ✅ Precise null marking for arrays (nullable array vs nullable elements)
12. ✅ Null marking for collections with nullable elements
13. ✅ Spring Framework's `@Contract` annotation integration
14. ✅ Using `@Nullable` parameters for optional method arguments
15. ✅ How NullAway validates complex generic types and record fields
16. ✅ Combining Spring's `@RequestParam` with null safety annotations
