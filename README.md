# TDD and function-first composition with Kotlin

This repository is a companion to the
 [Dependency Composition](https://martinfowler.com/articles/dependency-composition.html)
that discusses an alternative approach to building services using a somewhat unconventional combination of TDD,
partial application, and functions as the primary compositional unit. The article is written using Typescript
examples, but this is a port for Kotlin, which means the pattern is necessarily a little bit different.

# Points of interest

- Each component module defines a type with its own dependent functions.
- At the top level of the injection is done via the `Application.kt`
- Rather than constructors for components, modules expose a factory function that takes dependencies and
  returns either a "configured" function or component.

# Differences from the Typescript Example

Typescript's structure typing makes some things possible that are not in a language like Kotlin which has a nominal type
systems, meaning two types are considered different even if they are identical in composition. Most of the deviations
from the original pattern come from this difference, although there are a few others.

The most significant difference is that a number of entities, all in the `domain` package exist to be consumed by
multiple layers of the application. This creates incidental coupling across components. If that coupling is
the source of significant problems, another option is to inject adapters that convert, for example the `Restaurant`
type from the service layer (`TopRated.kt`) to that in the repository layer (`RestaurantRepository`). For an application
of this simplicity where the entity would be identical, it's probably not worth it. The downside is that types cross
layer boundaries are subject to use by new code which can start to create a tangle.

For an example of adapting entities, look at how `RestaurantWireType` is converted to `Restaurant` in
`Controller.createTopRatedController`. This strategy can be adopted between any two tiers to keep layers clean, but
always at the cost of additional template code.

It's worth noting that I've tried to be quite literal about the port to Kotlin, even if it meant occasionally
deviating from Kotlin idioms. Some examples are:

* using partially applied functions when native Kotlin delegation might have served
* interfaces that contain function variables rather than defining them with `fun`

I won't speculate whether it is more or less "correct" to make these compromises. I can see both sides of the argument
and decided to try this approach. If time permits, I'll make some changes to be more Kotlinesque to compare the two.

# Instructions

## Running the unit tests

    ./gradlew test

## Running the integration tests

    ./gradlew integrationTest

## Running the application

    docker-compose up -d
    REVIEW_DATABASE_USER=postgres REVIEW_DATABASE_PASSWORD=postgres \
      REVIEW_DATABASE_JDBC_URL=jdbc:postgresql://localhost:5432/postgres ./gradlew run

You should then be able to access the service endpoint at `http://localhost:8080/restaurants/recommended`

