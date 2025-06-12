# Sehat Medika MS - Healthcare Microservices Platform

> **Transforming Healthcare, Empowering Lives Daily**

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)

## 📋 Overview

**IF2050-2025-K1J-Sehat_Medika_MS** is a comprehensive microservices platform tailored for healthcare application development, featuring organized build management and extensive source code components. It streamlines the creation of scalable, modular healthcare systems with a focus on maintainability and legal compliance.

This project offers a robust foundation for building healthcare management solutions that can handle complex healthcare workflows while maintaining high standards of security and compliance.

## ✨ Core Features

- **🧩 Modular Architecture**: Multi-project build setup with `settings.gradle` for organized microservices architecture
- **⚙️ Cross-Platform Build**: Automated build system using Gradle wrapper scripts for consistent deployment
- **📝 Developer-Friendly**: Comprehensive developer guidance for Java setup within Visual Studio Code
- **🔒 Compliance Ready**: Legal and licensing documentation ensuring compliance with open-source and healthcare standards
- **🚀 Production Ready**: Extensive source code for entities, DAOs, controllers, and views supporting healthcare workflows
- **🔧 Scalable Design**: Modular design enabling seamless integration and scalable development

## 🛠️ Technology Stack

- **Programming Language**: Java
- **Build Tool**: Gradle
- **Database**: MySQL
- **Architecture**: Microservices
- **IDE Support**: Visual Studio Code optimized

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK)** 8 or higher
- **Gradle** (or use the included Gradle wrapper)
- **MySQL** database server
- **Git** for version control
- **Visual Studio Code** (recommended IDE)

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/AndhikaAddiputra/IF2050-2025-K1J-Sehat_Medika_MS.git
cd IF2050-2025-K1J-Sehat_Medika_MS
```

### 2. Build the Project

Using Gradle wrapper (recommended):
```bash
./gradlew build
```

Or using system Gradle:
```bash
gradle build
```

### 3. Database Setup

1. Create a MySQL database for the application
2. Configure database connection in the application properties
3. Run database migrations if available

### 4. Run the Application

```bash
./gradlew Run
```

## 📁 Project Structure

```
IF2050-2025-K1J-Sehat_Medika_MS/
├── settings.gradle              # Multi-project build configuration
├── gradlew                      # Gradle wrapper script (Unix)
├── gradlew.bat                  # Gradle wrapper script (Windows)
├── gradle/                      # Gradle wrapper files
├── src/                         # Source code directory
│   ├── main/
│   │   ├── java/               # Java source files
│   │   │   ├── entities/       # Data entities
│   │   │   ├── dao/            # Data Access Objects
│   │   │   ├── controllers/    # REST controllers
│   │   │   └── views/          # View components
│   │   └── resources/          # Configuration files
│   └── test/                   # Test files
```

## 🏥 Healthcare Features

This platform includes specialized components for healthcare management:

- **Patient Management**: Comprehensive patient record handling
- **Medical Records**: Secure storage and retrieval of medical data
- **Appointment Scheduling**: Efficient appointment management system
- **Healthcare Provider Management**: Tools for managing healthcare professionals
- **Compliance Features**: Built-in compliance with healthcare regulations
- **Security**: Enhanced security measures for sensitive healthcare data

## 🔧 Development Setup

### Visual Studio Code Configuration

1. Install the Java Extension Pack
2. Configure Java runtime in VS Code settings
3. Set up Gradle extension for build automation
4. Configure database connection settings

### Building and Testing

```bash
# Run all tests
./gradlew test

# Build without tests
./gradlew build -x test

# Clean build
./gradlew clean build
```

## 📊 Architecture Overview

The system follows a microservices architecture pattern with:

- **Service Layer**: Business logic implementation
- **Data Access Layer**: Database interaction through DAOs
- **Controller Layer**: RESTful API endpoints
- **View Layer**: User interface components
- **Configuration Layer**: Environment and database configurations


## 📄 License

This project is to fulfill final project of IF2050 Fundamental of Software Engineering. All rights reserved for academic purposes only.

## 👥 Team

- **Course**: IF2050 - Fundamental of Software Engineering
- **Academic Year**: 2025
- **Class**: K01 - J
- **Project Team**: 
    1. Andhika Maulana Addiputra - 18223005 (Frontend & PM)
    2. Ferro Arka Berlian - 18223027 (Backend)
    3. Luckman Fakhmanidris A - 18223041 (Backend)
    4. Muhammad Omar Berliansyah - 18223055 (Frontend)
    5. Muhammad Naufal Fathan	- 18223059 (Frontend)
    6. Atharizza Muhammad Athaya - 18223079 (Backend)


## 🗓️ Roadmap

- [ ] Enhanced patient portal features
- [ ] Integration with external healthcare APIs
- [ ] Advanced analytics and reporting
- [ ] Telemedicine capabilities

## ⚡ Performance & Scalability

The microservices architecture ensures:

- **Horizontal Scalability**: Individual services can be scaled independently
- **Fault Isolation**: Issues in one service don't affect others
- **Technology Diversity**: Different services can use different technologies
- **Deployment Flexibility**: Services can be deployed and updated independently



*Empowering healthcare providers with modern, scalable, and compliant technology solutions.*
