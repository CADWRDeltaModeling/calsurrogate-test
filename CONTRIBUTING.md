<!-- omit in toc -->
# Contributing to 2023 DCR Base Studies

See the [Table of Contents](#table-of-contents) for information about getting the study set up on your machine, and how to make make changes to the `master` branch.

- [CVM-Inventory vs Repository](#cvm-inventory-vs-repository)
- [Branch Management](#branch-management)
- [Pull Requests](#pull-requests)
- [Commit Messages](#commit-messages)

# CVM Inventory vs Repository

There are three locations that DCR resources live:

1. In this repository
    - Anything neccesary to run CalSim3
    - Source code like `.wresl`, `.table`, or `.launch` files
2. Within the `\Investigations` folder on `\\cnrastore-bdo\CVM_Inventory\_DeliveryCapabilityReport\2023_DCR_Studies`
    - Documentation on the motivation for changes
    - Results analysis
    - Any other contextual information
    - A copy of the final DV `.dss` file for the investigation 
3. Within the `\DCR2023_Base_Development` folder on `\\cnrastore-bdo\CVM_Inventory\_DeliveryCapabilityReport\2023_DCR_Studies`
    - If an `inv` branch is pulled into `master`, a release file should be made in this location with a version number
    - A tag should be made on the corresponding commit on the `master` branch

# Branch Management

The project is using a simplified version of [GitFlow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow) for branch management. Typical development involves two types of branches:

- `master`
    - Each commit on master should be a stable version of the DCR
    - Commit messages on `master` should summarize the changes made to inputs, and the effects on the results of the model 
- `inv-`
    - Investigation branches should be opened for improvements, explorations, and standard changes to the DCR
    - Commit messages should follow [these basic guidelines](https://commit.style/)
- `doc-`
    - Documentation branches shouldn't edit anything that would effect the model results

The project may open the following branch types on a case by case basis:

- `bugfix`/`hotfix`/`release`

The project may add release tags at relevant points in the project history.

# Making a contribution

## 1. Create a new __investigation__ branch 
- Branch name should start with the stem `inv-` , and follow the kebab case style
- The name should be:
    - descriptive
    - specific to the work being done 
    - not reference the developer of the branch

__Bad branch names would be `inv-updates`, or `inv-zach-roy-work`.__

__Good branch names would be `inv-flow-feather-river`, or `inv-delivery-mwd`.__

## 2. Create an Investigation folder on `CVM-Inventory`
- `\\cnrastore-bdo\CVM_Inventory\_DeliveryCapabilityReport\2023_DCR_Studies\Investigations`
- Add a `.branch` file with that contains the corresponding name of the `inv` branch.

## 3. Make your model changes
- Commit changed input files every time you run the model, this helps keep track of your train of thought when making changes
- Commit messages should follow the style guide below
- Do not commit outputs at every step in the investigation branch

## 4. Open a Pull Request on `master`
- See the [section](#pull-requests) below

## 5. Create a folder in the Sequence Directory
- `\\cnrastore-bdo\CVM_Inventory\_DeliveryCapabilityReport\2023_DCR_Studies\DCR2023_Base_Development`
- This folder should like to the Investigations folder on `CVM_Inventory`

## 6. Tag the commit on the main branch
- Use the same number as what you used in the sequence dorectory 

# Pull Requests

Pull requests need to be opened on `master` when incorporating work on a investigation branch.

Pull requests should:

- Start with the stem `WIP` so they are not automatically given a release number 
- Use a title that references the branch being incorporated
- Include a discussion of:
    - Motivation for the updates
    - Conceptually, what was changed to satisfy the motivation
    - Technically, what was changed in the model inputs
    - Conceptually, how the results changed
- Include links to any relevant Gitea Issues that are being addressed by the branch

# Branch Management Visual Example                                    
```                          
v0.1                              v0.2
A----------------------K----------O     master
|\                     |\         |
| B---C---D            | |        |     inv-change-solver
|\                    /  |        |    
| ---------------I---J   |        |     inv-adjust-demands
\                        |        /
 E---F---G---H-----------L---M---N      inv-extend-precip
```

Notes:

- Pull requests should be opened prior to commits `K` and `O` on `master`.
- The `inv-change-solver` branch was never merged
- Changes in `K` were pulled into the investigation branch `inv-extend-precip` prior to a pull request
- Tags on `A` and `O` are case-by-case, not every commit on `master` warrents a release tag

# Commit Messages

Commit messages should follow the [simple style guide from Tim Pope](https://commit.style/).

```
Commit message style guide for Git

The first line of a commit message serves as a summary.  When displayed
on the web, it's often styled as a heading, and in emails, it's
typically used as the subject.  As such, you should capitalize it and
omit any trailing punctuation.  Aim for about 50 characters, give or
take, otherwise it may be painfully truncated in some contexts.  Write
it, along with the rest of your message, in the imperative tense: "Fix
bug" and not "Fixed bug" or "Fixes bug".  Consistent wording makes it
easier to mentally process a list of commits.

Oftentimes a subject by itself is sufficient.  When it's not, add a
blank line (this is important) followed by one or more paragraphs hard
wrapped to 72 characters.  Git is strongly opinionated that the author
is responsible for line breaks; if you omit them, command line tooling
will show it as one extremely long unwrapped line.  Fortunately, most
text editors are capable of automating this.
```