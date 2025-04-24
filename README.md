# Build-To-Order (BTO) Management System 

[BTO Management System Link](https://github.com/tanck04/btoms)

This project is a group assignment for Nanyang Technological University's (NTU) SC2002 module on Object-Oriented Programming. The BTO Management System application simulates key Housing & Development Board (HDB) processes, including project listings, officer registration, application submission, and approval workflows.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Dependencies](#dependencies)
- [Configuration](#configuration)
- [Troubleshooting](#troubleshooting)
- [Contributors](#contributors)

## Introduction

The BTO Management System streamlines the digital simulation of BTO applications and officer/manager roles in managing housing projects. It is designed for educational purposes to demonstrate core object-oriented principles such as inheritance, encapsulation, and abstraction through a real-world scenario.

## Features

- **Applicant Functions:**
  - Submit applications for BTO projects
  - View and withdraw submitted applications
  - Track application and registration status
- **Officer Functions:**
  - Submit application as an applicant
  - Register to handle BTO projects
  - Book flats for successful applicants
  - View and reply to applicants' enquiries
- **Manager Functions:**
  - Filter housing projects by neighbourhood and flat type
  - Review officer registration and application status
  - View and reply to applicants' enquiries

## Installation

To set up the BTO Management System locally:

1. **Clone the repository:**

   ```bash
   git clone https://github.com/tanck04/btoms
   ```

2. **Navigate to the project directory:**
   ```bash
   cd main
   ```
3. **Compile the application:**

   ```bash
   javac -d bin src\main\BTOMain.java
   ```

4. **Run the application:**
   ```bash
   java -cp bin main.BTOMain
   ```

## Usage

Upon running the application, users will be prompted to log in as either an Applicant, Officer, or Manager. Each role has its own set of functionalities accessible via a dynamic menu system.

## Dependencies

- **Java Development Kit (JDK):** Ensure that JDK version 22 is installed on your system.

## Configuration

No additional configuration is required. The application runs with default settings suitable for a local environment.

## Troubleshooting

- **Compilation Errors:**

  Ensure that all Java source files are present in the `src` directory and that you have the correct JDK version installed. (JDK - 22)

## Contributors

This project was developed by a group of students as part of the SC2002 module at NTU. The contributors include:

- AGARWAL DHRUVIKAA, U2423574H
- HE HAOYU, U2420885A
- KHOR HAOJUN, U2420982C
- LEE BECKHAM, U2422260H
- TAN CHUEN KEAT, U2420792G
