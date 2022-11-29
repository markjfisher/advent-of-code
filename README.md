# Advent of code

My kotlin solutions to [Advent of Code](https://adventofcode.com/) puzzles.

To run all days, adjust year, but run with:

    ./gradlew advent2021

## windows

To run tests, the files must be in unix format when checked out of git, so use

```shell
git config --global core.eol lf
git config --global core.autocrlf input

# if you had checked it out without setting above first, you need to fix files with CRLF into LF:
git rm -rf --cached .
git reset --hard HEAD
```