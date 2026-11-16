# Staffbase fork

`Staffbase/sempods-kotlin` is upstream [sempods/sempods-kotlin](https://github.com/sempods/sempods-kotlin)
plus a short, visible list of patches. Every patch is either on its way upstream or a hotfix with an
expiry date. The goal is a list of zero: then the backend pins an upstream release again.

This file, [`.github/workflows/staffbase-release.yml`](.github/workflows/staffbase-release.yml) and
[`.github/staffbase/`](.github/staffbase/) exist only on `staffbase/*` branches. They never go upstream.
Why the backend consumes the fork at all:
[ADR0003](https://github.com/Staffbase/backend/blob/main/modulith/sempods/docs/adrs/0003-consume-sempods-kotlin-through-a-staffbase-fork.md).

## Branches

| Branch                        | Holds                                                   | Based on                  |
|-------------------------------|---------------------------------------------------------|---------------------------|
| `staffbase/<version>`         | one fork release line: upstream tag + patches + tooling | upstream tag `v<version>` |
| `feat/<topic>`, `fix/<topic>` | one topic, later exactly one upstream pull request      | `upstream/main`           |
| `backport/<topic>-<version>`  | the topic's commits cherry-picked onto a release line   | `staffbase/<version>`     |

`<version>` is the full upstream version, e.g. `staffbase/0.2.0`. Each upstream release, patch releases
included, gets its own line, and its fork releases are `<version>-staffbase.<n>`.

The fork's `main` is not used. It sits behind Staffbase organisation rulesets and may lag upstream;
always branch from `upstream/main` directly.

## Rules

* **The fork is public.** No Jira keys, internal links, customer names or other internal details in
  branch names, commits, pull requests or code.
* **Topic branches follow upstream's rules from the first commit:** [CONTRIBUTING.md](CONTRIBUTING.md),
  [AGENTS.md](AGENTS.md), `git commit -s` for the DCO sign-off, tests and documentation in the same
  change. Otherwise the upstream pull request needs a rewrite.
* **Never open a pull request from `staffbase/*` or `backport/*` to upstream.**
* **No commit on a release line without a topic** — except this tooling and version bumps.

## Setup

```bash
git clone git@github.com:Staffbase/sempods-kotlin.git && cd sempods-kotlin
git remote add upstream https://github.com/sempods/sempods-kotlin.git
git fetch upstream --tags
git config rerere.enabled true
```

## A change

1. Branch from upstream: `git switch -c feat/<topic> upstream/main`. Commit with `git commit -s`.
2. Push to the fork and open the pull request against `sempods/sempods-kotlin:main` right away —
   a draft is fine. Add a row to [Patches](#patches).
3. If the backend needs it before upstream releases it, backport it:

   ```bash
   git switch -c backport/<topic>-0.2.0 origin/staffbase/0.2.0
   git cherry-pick -x -s <commits of feat/<topic>>
   ```

   Open a pull request into `staffbase/0.2.0`, then [release](#a-release).

`-x` records the original commit, so `git log staffbase/0.2.0` shows where each patch came from.

A **hotfix** is the same path, faster: open the upstream pull request and the backport together.

## A release

1. Pull request into `staffbase/<version>` that sets `version=<upstream version>-staffbase.<n>` in
   `gradle.properties`, e.g. `0.2.0-staffbase.1`. Merge it.
2. Tag the merged commit and push the tag:

   ```bash
   git tag -a v0.2.0-staffbase.1 -m "0.2.0-staffbase.1" && git push origin v0.2.0-staffbase.1
   ```

3. `staffbase-release.yml` checks the version, runs the suites and publishes every module and
   `sempods-bom` to the Staffbase Artifact Registry. Never to Maven Central.
4. In the backend, set `org.sempods:sempods-bom` in `platform/build.gradle.kts` to the new version.

## A new upstream release

Upstream releases `v0.3.0` (a patch release such as `v0.2.1` works the same way):

1. `git switch -c staffbase/0.3.0 v0.3.0`, then cherry-pick the tooling commit from `staffbase/0.2.0`.
2. For each row in [Patches](#patches): released upstream → delete the row; otherwise cherry-pick it
   onto `staffbase/0.3.0`. `git range-diff` shows what changed while doing so.
3. No rows left → the backend pins `org.sempods:sempods-bom:0.3.0` and no fork release is needed.

`staffbase/0.2.0` stays as it is; it is never rebased.

## Patches

The complete delta between the fork and upstream. `git log v0.2.0..origin/staffbase/0.2.0` shows the
tooling commit, version bumps and the patches listed here — nothing else.

| Topic branch | Upstream pull request | In fork release | Status |
|--------------|-----------------------|-----------------|--------|
| —            | —                     | —               | —      |
