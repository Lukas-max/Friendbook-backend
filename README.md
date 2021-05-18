# FriendBook
## Backend part
Project by Łukasz Jankowski.

### Prerequisites
- JDK 11
- Maven
- PostgreSQL database
### Build in:
- Spring Boot
- Spring Data JPA
- Spring Websocket/STOMP
- Spring Security and JWT
- Spring Validation
- Spring Java Mail
- Thymeleaf (HTML email templates for account activation, password reset etc..)
- PostgreSQL
- IntelliJ IDEA 2020.1 Ultimate Edition

### Other:
- jjwt 0.9.1
- Lombok
- Model Mapper 2.3.9
- Apache Tika 1.12

### Testing:
- Postman

This is the back end of the application. The other part -> [FriendBook - FrontEnd part!](https://github.com/Lukas-max/Friendbook-client). To run the whole thing you need both! 
schema.sql for generating databse tables is written in PostgreSql, remember that.

## Run
You need to create your own application.properties. Configuration: 
```
spring.main.banner-mode=off
server.port=[your port]

app.mail.verification.mode=ON
app.mail.verification.url=http://localhost:4200/register-verify
app.mail.reset-password.url=http://localhost:4200/reset
jwt.secret.key= [your key]

spring.datasource.url=jdbc:postgresql://localhost:5432/[your database here]?useSSL=false&serverTimezone=UTC
spring.datasource.username=postgres
spring.datasource.password=admin
#spring.jpa.hibernate.ddl-auto=create
spring.jpa.hibernate.ddl-auto=none
spring.datasource.initialization-mode=always

spring.mail.host = smtp.gmail.com
spring.mail.port = 587
spring.mail.username = 
spring.mail.password = 
spring.mail.properties.mail.smtp.starttls.enable = true
spring.mail.properties.mail.smtp.auth = true
spring.mail.properties.mail.smtp.connectiontimeout = 5000
spring.mail.properties.mail.smtp.timeout = 5000
spring.mail.properties.mail.smtp.writetimeout = 5000

spring.servlet.multipart.max-file-size=
spring.servlet.multipart.max-request-size=
```
- You can copy that config and paste in your values.
- jwt.secret.key - that is the secret key for creating jwt token
- app.mail.verification.mode - when ON usere register will be by Email verification using verification token
- app.mail.verification.url - with this url will be created the email verication link, where the token will be passed
- app.mail.reset-password.url - etc, but when reseting the password when password was forgotten by user
- for mail sending we have here picked gmail mail. You can choose whatever you like

Now when this is done you need to be sure that with your first start you need to:
1) Create database tables and fill them with thata by:
:ballot_box_with_check: make sure that in application.properties you have :
 ```
 #spring.jpa.hibernate.ddl-auto=create
spring.jpa.hibernate.ddl-auto=none
spring.datasource.initialization-mode=always
```
(You can always use ddl-auto instead of schema.sql generation)
:ballot_box_with_check: The class AppBoot.java has active annotation @Component so it will be in Spring Context
- 
- 2) Create folders for users to store files:
:ballot_box_with_check: The class AppBoot.java has active annotation @Component so it will be in Spring Context

:exclamation::exclamation::exclamation: WARNING :exclamation::exclamation::exclamation:
Remeber that AppBoot.java - fileStorage.init() and feedStorage.init() will always wipe out first ALL your folders and files. Then create new folder space anew. :warning:

Ok now you can start the application.

## Endpoints
### ChatDataController
Used to retrieve public chat messages, p2p user messages and pending messages of the authorized user

| Method | URI | Action | Security |
|--------|-----|--------|----------|
|  `GET` | `/api/chat` | `Get Chunk of public messages` | `USER` |
| `GET` | `/api/chat/{senderUUID}/{receiverUUID}` | `Get Chunk of p2p messages` | `USER` |
| `GET` | `/api/chat/pending-messages` | `Get pending messages` | `USER` | 

### FileController
Used to work with user files and directories. Storing files, creating, deleting folders in user directory,

| Method | URI | Name | Action | Security | Other |
|--------|-----|------|--------|----------|-------|
|  `GET` | `/api/storage` | `getUserDirectories` | `Get array with user folders` | `USER` | `Params:  userUUID` |
|  `GET` | `/api/storage/files` | `getFileData` | `Get Chunk<FileData>` | `USER` | `Params:  userUUID, directory, limit, offset` |
|  `GET` | `/api/storage/file/{userUUID:.+}/{directory:.+}/{fileName:.+}` | `downloadFile` | `Get file in byte[]` | `USER` | `Params:  none` |
|  `GET` | `/api/storage/image/{id:.+}/{directory:.+}/{fileName:.+}` | `downloadImage` | `Get image in byte[]` | `USER` | `Params:  none` |
|  `GET` | `/api/storage/profile/high-quality/{userUUID}` | `downloadProfileHighQuality` | `Get profile photo` | `USER` | `Params:  none` |
|  `GET` | `/api/storage/profile/low-quality/{userUUID}` | `downloadProfileLowQuality` | `Get profile photo` | `USER` | `Params:  none` |
|  `POST` | `/api/storage` | `uploadFiles` | `Upload array of files to dir` | `USER` | `Body: files[], Params:  directory` |
|  `POST` | `/api/storage/images` | `uploadFilesAndImages` | `Upload array of files to dir` | `USER` | `Body: files[],images[] Params:  directory` |
|  `POST` | `/api/storage/profile` | `uploadProfilePhoto` | `Upload profile photo` | `USER` | `Body: photo` |
|  `POST` | `/api/storage/directory` | `createFolder` | `Create folder in user dir` | `USER` | `Body: directory` |
|  `DELETE` | `/api/storage/{directory}` | `deleteFolder` | `Delete folder from user dir` | `USER` | `Nope` |
|  `DELETE` | `/api/storage/{directory}/{fileName}` | `deleteFile` | `Delete file` | `USER` | `Nope` |
|  `DELETE` | `/api/storage/profile` | `deleteProfilePhoto` | `Delete prophile photo` | `USER` | `Nope` |
