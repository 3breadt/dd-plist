# Security Policy

## Supported versions

dd-plist aims to remain backward compatible and does not plan to introduce
breaking changes. The 1.x release line is therefore the only major release line
and is supported with security updates.

| Version | Supported |
| ------- | --------- |
| 1.x     | Yes       |
| < 1.0   | No        |

Security fixes are released in the latest 1.x version. Users should update to
the latest available release to receive them.

## Reporting a vulnerability

Please use GitHub to
[privately report a vulnerability](https://github.com/3breadt/dd-plist/security/advisories/new).
This is the preferred reporting channel because it provides a private place to
discuss, investigate, and coordinate a fix.

If you cannot use GitHub private vulnerability reporting, email Daniel
Dreibrodt at
[daniel.dreibrodt@gmail.com](mailto:daniel.dreibrodt@gmail.com). Do not open a
public GitHub issue or discussion before a fix and disclosure have been
coordinated.

Include as much of the following information as possible:

- the affected version and environment;
- a description of the vulnerability and its potential impact;
- steps or a minimal example that reproduce the issue; and
- any known mitigations or suggested fixes.

If the report contains sensitive information, you may encrypt it with the
OpenPGP key in [`KEYS`](KEYS). Its fingerprint is:

```text
57A9 B92F EEDC 551C 3A5E  5E5F 6373 8466 88F5 87B4
```

You can expect an acknowledgement within seven days. The maintainer will
investigate the report, keep you informed of material progress, and coordinate
the timing and content of any public disclosure. Please allow a reasonable
amount of time for a fix before disclosing the vulnerability publicly.

## Verifying release artifacts

Release artifacts have detached OpenPGP signatures with an `.asc` suffix. The
[`KEYS`](KEYS) file follows the
[Apache release-signing guidance](https://infra.apache.org/release-signing#keys-policy).

Download the artifact and its `.asc` file from the same Maven Central directory,
then verify them with GnuPG:

```sh
gpg --import KEYS
gpg --verify dd-plist-<version>.jar.asc dd-plist-<version>.jar
```

Replace `<version>` with the release version. Ensure that GnuPG reports a good
signature from the full fingerprint documented above.
