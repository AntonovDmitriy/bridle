# Bridle

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/1dff818ad7d24c1ab49e16c78fc7f648)](https://app.codacy.com/gh/antonovdmitriy/bridle/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=AntonovDmitriy_bridle&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=AntonovDmitriy_bridle)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=AntonovDmitriy_bridle&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=AntonovDmitriy_bridle)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=AntonovDmitriy_bridle&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=AntonovDmitriy_bridle)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=AntonovDmitriy_bridle&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=AntonovDmitriy_bridle)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=AntonovDmitriy_bridle&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=AntonovDmitriy_bridle)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=AntonovDmitriy_bridle&metric=coverage)](https://sonarcloud.io/summary/new_code?id=AntonovDmitriy_bridle)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=AntonovDmitriy_bridle&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=AntonovDmitriy_bridle)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=AntonovDmitriy_bridle&metric=bugs)](https://sonarcloud.io/summary/new_code?id=AntonovDmitriy_bridle)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=AntonovDmitriy_bridle&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=AntonovDmitriy_bridle)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=AntonovDmitriy_bridle&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=AntonovDmitriy_bridle)

## Overview
**Bridle** is a convenient, configurable application designed for handling the most common integration tasks, built on the experience of using Apache Camel and Spring Boot.

## Technology Stack
- Apache Camel 3
- Spring Boot
- Java 17

## Why is this project needed?
Apache Camel offers many components and features for writing integration routes using enterprise integration patterns. According to the author's experience, it is possible to generalize typical integration scenarios and create a convenient tool for everyday use in projects where integration is needed.

The second reason for the project's creation is the author's dissatisfaction with the convenience of configuring Apache Camel components and endpoints. It is possible to configure a component via Spring auto-configurations, but they only work for one component of each type. The author has encountered many situations when it would be necessary to add a component or endpoint setting from the list of possible ones, but the configuration did not allow this without rebuilding the application.

The third reason is the creation of standard routes, which, if desired, can be copied to third-party projects and make custom changes for specific integration tasks. The current examples in the Apache Camel project do not, in the author's opinion, represent real examples typical of enterprise applications. The author attempts to account for the many mandatory components that are present in the actual project. (validation, logging, metrics, authentication, etc.)

## Project Structure
(TBD)

## Usage Examples
(TBD)

