# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [Unreleased]

- *No changes yet*

## [1.30.0] - 2026-07-24

### Security

- Hardened binary property list parsing with bounds, length, offset-table, and
  object-reference validation for malformed or truncated input.
- Limited binary and XML property lists to 512 nested objects to prevent stack
  overflows from excessively nested input.

### Added

- Added a security policy and the public keys used to verify releases.
- Added changelog markdown file

## [1.29.0] - 2026-02-19

### Changed

- Replaced Travis CI with GitHub Actions and upgraded the build pipeline.
- Switched Maven Central deployment to the Central Publishing Maven Plugin.
- Upgraded Maven plugins
- Corrected Moditect integration (`module-info.java`)

## [1.28.0] - 2024-03-10

### Added

- Added source location information to parsed objects and parse exceptions.
- Added a generated `module-info.java` through Moditect while preserving Java
  8 compatibility.

### Fixed

- Corrected the reported error offset for invalid quoted ASCII strings.
- Made tests account for the exception produced by OpenJDK 17 on Ubuntu.

## [1.27.0] - 2023-04-10

### Added

- Added support for Base64 data in GNUstep ASCII property lists.
- Improved support for storing `null` values in `NSArray` and `NSSet`.

### Changed

- Overhauled the `UID` implementation.
- Optimized binary parsing when many references point to the same object.
- Invalid `NSNumber` strings in XML and ASCII property lists now produce parse
  exceptions.
- Unexpected XML document types and root nodes now produce
  `PropertyListFormatException` instead of `UnsupportedOperationException`.

### Fixed

- Prevented stack overflows when binary property lists contain cyclic
  references.
- Replaced several runtime exceptions with format exceptions for malformed,
  truncated, or otherwise invalid property lists.
- Fixed index-out-of-range failures in the binary parser.
- Fixed parsing of infinity values in XML property lists.
- Made `equals(null)` return `false` instead of throwing.

## [1.26.0] - 2022-10-29

### Changed

- Reduced binary parser memory use by reading object offsets on demand instead
  of allocating an in-memory offset table.

## [1.25.0] - 2022-09-17

### Added

- Added XML and ASCII parser entry points for `String` and `Reader` input.

### Changed

- Moved property list writing and conversion logic into dedicated writer
  classes.
- Reviewed and cleaned up the XML and ASCII parsers.

## [1.24.0] - 2022-07-23

### Changed

- Raised the minimum supported Java version to Java 8.
- `XMLPropertyListParser.parse(InputStream)` no longer closes the caller-owned
  stream.

### Fixed

- Excluded illegal XML characters when serializing `NSString` values.
- Improved handling of malformed binary property lists to avoid delayed
  `ArrayIndexOutOfBoundsException` failures.
- Fixed XML parsing for input beginning with a byte order mark.

## [1.23] - 2019-11-06

### Added

- Added `NaN` support to `NSNumber`.

### Changed

- Migrated the test suite to JUnit 5.

## [1.22] - 2019-08-22

### Fixed

- Fixed conversion of integer `NSNumber` values to `float` and `double`.

## [1.21] - 2018-08-21

### Added

- Made property list objects cloneable.

### Changed

- Improved input and output stream handling.
- Improved detection and handling of text encodings in ASCII property lists
  that are not strictly ASCII.
- Strengthened binary property list consistency checks, including validation
  of the object offset table.

### Fixed

- Fixed parsing of escaped special characters in ASCII property lists.
- Improved error reporting for invalid ASCII escape sequences.

## [1.20] - 2017-07-20

### Added

- Added value conversion methods to `NSString`.
- Added `NSNumber.stringValue()`.

### Changed

- Removed unnecessary synchronization.

### Fixed

- Fixed getter and setter name handling during `NSObject` serialization and
  deserialization.

## [1.19] - 2017-03-05

### Changed

- Disabled automatic decompression of GZIP input.

### Fixed

- Corrected an incomplete example in the documentation.

## [1.18] - 2017-02-04

### Added

- Added conversion between `NSObject` values and Java objects.
- Added support for empty property list files.
- Added parsing of unescaped UTF-8 characters in quoted strings.
- Added XML external entity protections and restricted external resource
  resolution to the bundled property list DTD.
- Exposed `XMLPropertyListParser.getDocBuilder()`.

### Changed

- **Breaking:** Replaced `NSObject.wrap(Object)` with
  `NSObject.fromJavaObject(Object)` and corrected Java object conversion.
- Raised the minimum supported Java version from Java 5 to Java 6.
- Preserved dictionary iteration order consistently across Java versions.
- Improved performance when parsing very long unterminated strings.

### Fixed

- Corrected binary dictionary object ID assignment.
- Made equality comparisons symmetric for subclasses.
- Fixed parsing of selected system property lists.

## [1.17] - 2015-07-23

### Added

- Made `XMLPropertyListParser.parse(Document)` public.

### Changed

- Reduced temporary allocations in the binary parser.
- Moved project references from Google Code to GitHub.

### Fixed

- Prevented binary stream parsing from reading beyond the property list data.
- Made the binary parser reject unknown or unsupported object types with a
  parse exception.
- Renamed tests so Maven Surefire executes them.

## [1.16] - 2015-02-27

### Added

- Added OSGi metadata.
- Added more specific parser exception types.

### Changed

- Unquoted numeric-looking strings in ASCII property lists are retained as
  strings.
- Dictionaries ignore `null` values instead of throwing
  `NullPointerException`.

### Fixed

- Fixed property list format detection.
- Fixed binary property lists whose size is a multiple of 512 bytes.
- Fixed XML and ASCII parsing with a Unicode byte order mark.
- Fixed handling of `null` values during serialization.
- Fixed array wrapping and comparison.

## [1.3] - 2013-05-10

### Added

- Added mutable `NSString` and `NSDictionary` APIs.
- Added ASCII representations for `NSSet`.
- Added saving property lists directly to output streams.
- Added support for comments in ASCII property lists.
- Added deep conversion between property list objects and native Java objects.
- Made `NSDictionary` implement `Map<String, NSObject>`.
- Added ordered-set support for formats that support it.

### Changed

- Property list input that is neither binary nor ASCII is treated as XML.
- Constructors intended for extension are now protected.
- Restored Java 5 compatibility by removing Java 7 API usage.
- Boolean strings are parsed more strictly.

### Fixed

- Fixed XML property lists with a byte order mark or without a `plist` element.
- Fixed ASCII strings that begin with numbers and corrected comment skipping.
- Fixed deep conversion of arrays.
- Fixed escaping when writing standard ASCII property lists.
- Improved UTF-8 encoding error handling.

## [1.0] - 2012-08-26

### Added

- Added parsing and writing for XML, binary, and ASCII property lists.
- Added support for property list arrays, dictionaries, strings, numbers,
  dates, data, sets, and UIDs.
- Added parsing from files, input streams, and byte arrays.
- Added binary and ASCII property list serialization.
- Added Java 5 and Android compatibility.
- Added thread-safe XML and binary parsing and date/string serialization.
- Adopted the MIT license and Maven build.

[Unreleased]: https://github.com/3breadt/dd-plist/compare/v1.30.0...HEAD
[1.30.0]: https://github.com/3breadt/dd-plist/compare/v1.29.0...v1.30.0
[1.29.0]: https://github.com/3breadt/dd-plist/compare/v1.28.0...v1.29.0
[1.28.0]: https://github.com/3breadt/dd-plist/compare/v1.27.0...v1.28.0
[1.27.0]: https://github.com/3breadt/dd-plist/compare/v1.26.0...v1.27.0
[1.26.0]: https://github.com/3breadt/dd-plist/compare/v1.25.0...v1.26.0
[1.25.0]: https://github.com/3breadt/dd-plist/compare/v1.24.0...v1.25.0
[1.24.0]: https://github.com/3breadt/dd-plist/compare/dd-plist-1.23...v1.24.0
[1.23]: https://github.com/3breadt/dd-plist/compare/dd-plist-1.22...dd-plist-1.23
[1.22]: https://github.com/3breadt/dd-plist/compare/dd-plist-1.21...dd-plist-1.22
[1.21]: https://github.com/3breadt/dd-plist/compare/dd-plist-1.20...dd-plist-1.21
[1.20]: https://github.com/3breadt/dd-plist/compare/dd-plist-1.19...dd-plist-1.20
[1.19]: https://github.com/3breadt/dd-plist/compare/dd-plist-1.18...dd-plist-1.19
[1.18]: https://github.com/3breadt/dd-plist/compare/dd-plist-1.17...dd-plist-1.18
[1.17]: https://github.com/3breadt/dd-plist/compare/dd-plist-1.16...dd-plist-1.17
[1.16]: https://github.com/3breadt/dd-plist/compare/dd-plist-1.3...dd-plist-1.16
[1.3]: https://github.com/3breadt/dd-plist/compare/dd-plist-1.0...dd-plist-1.3
[1.0]: https://github.com/3breadt/dd-plist/releases/tag/dd-plist-1.0
