SecureDocs: Your Files, Safely Stored


🔒 About the Project
SecureDocs is a robust and user-centric file management application designed to provide individuals with full control over their personal data. In an era where data privacy is a growing concern, SecureDocs offers a powerful solution that combines enterprise-grade security with an intuitive user experience.


Key Features:
1) Secure Authentication: A robust user registration and login system with BCrypt password hashing.

2) Password Recovery: Flexible password reset options via email token or a security question.

3) Encrypted File Storage: Documents are securely stored and tied directly to the user's account, ensuring data privacy.

4) File Management: A clean dashboard for users to easily upload, download, search for, and delete their documents.

5) Intuitive UI: A modern and responsive web interface built with HTML, CSS, and Thymeleaf.




🌟 Why Was It Created?
The project was born out of a desire to solve the fundamental problem of data ownership in cloud storage. Many popular platforms offer convenience but often leave users feeling disconnected from their data. SecureDocs was created to provide a viable alternative that prioritizes user control and peace of mind, proving that powerful security and a great user experience can coexist.




🛠️ Technology Stack
This application is built with a modern, secure, and scalable technology stack.

1) Backend: Spring Boot

2) Frontend: HTML, Thymeleaf, CSS

3) Security: Spring Security, BCryptPasswordEncoder

4) Database: JPA with Spring Data

5) Dependency Management: Maven




🚀 Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

Prerequisites
Java 17 or higher

Maven 3.6.0 or higher

A text editor or IDE (like IntelliJ IDEA or Visual Studio Code)

Installation
Clone the repository:

git clone [https://github.com/mayurwakikar/securedocs.git](https://github.com/mayurwakikar/securedocs.git)

Navigate to the project directory:

cd securedocs

Configure environment variables:
This project uses environment variables to manage sensitive data, such as email passwords, for security. Do not hard-code these values in application.properties file

For Windows (Command Prompt):

set SECURE_DOCS_APP_PASSWORD=your_actual_email_password

For macOS/Linux (Terminal):

export SECURE_DOCS_APP_PASSWORD="your_actual_email_password"

Replace your_actual_email_password with the app password for your email service (e.g., Gmail).

How to Run the Application
Build the project using Maven:

mvn clean install

Run the application from the command line:

mvn spring-boot:run

Access the application:
Open your web browser and navigate to http://localhost:8081.






🤝 Contribution
Contributions are what make the open-source community an amazing place to learn, inspire, and create. Any contributions you make are greatly appreciated.

1) Fork the Project

2) Create your Feature Branch (git checkout -b feature/AmazingFeature)

3) Commit your Changes (git commit -m 'feat: Add some AmazingFeature')

4) Push to the Branch (git push origin feature/AmazingFeature)

5) Open a Pull Request




👤 Author
Mayur Wakikar - Initial work - https://github.com/mayurwakikar

Email: mayurwakikar17@gmail.com

Website : https://sites.google.com/view/mayurwakikar

LinkedIn :- https://www.linkedin.com/in/mayur-wakikar-b96a29124/

Youtube :- https://youtube.com/@mayurwcodes

Instagram :- https://instagram.com/mayurwcodes

HackerRank :- https://www.hackerrank.com/profile/mayurwakikar17



🌐 Links
Project Link: https://github.com/mayurwakikar/SecureDocs
