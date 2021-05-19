# FriendBook
## Backend part
Project by Łukasz Jankowski.

### Prerequisites
- JDK 11
- Maven
- PostgreSQL database
### Build in:
- Spring Boot
- Spring JPA (EntityManager)
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


#### Project purpose and goal
The aim was to develop a functional application, from end to end, that people can use on the internet. First of all the target was to learn storing data, files in folders, developing chat between users, learning websockets, server sent events and user notification about changes in the app, building own lightbox and viewing different kinds of files. The second thing was to use this information and build the web app to better memorize and understand new functions. And least, to style it so it will be comfortable in use and not bad to look.

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
 - making sure that in application.properties you have :
 ```
 #spring.jpa.hibernate.ddl-auto=create
spring.jpa.hibernate.ddl-auto=none
spring.datasource.initialization-mode=always
```
(You can always use ddl-auto instead of schema.sql generation)
- The class AppBoot.java must have active annotation @Component so it will be in Spring Context and will upload data and create folders
 
2) Create folders for users to store files:
- The class AppBoot.java must have active annotation @Component so it will be in Spring Context

:exclamation::exclamation::exclamation: WARNING :exclamation::exclamation::exclamation:  
Remeber that AppBoot.java - fileStorage.init() and feedStorage.init() will always wipe out first ALL your folders and files. Then create folder space anew. :warning:

Ok now you can start the application.

## App main functions (Fullstack review)
Account:
- User authorization and authentication (with JSON Web Token)
- Register user with email verification
- Reseting password with email verification
- Email and password change by logged users
- Deleting account

File storage:
- Storing files in user file storage
- Deleting files
- Creating folders, deleting folders
- Adding profile photo, deleting the photo
- Each user has a limited storage space
- Informing user on used storage space 

Database:
- storing user data, main feed data, comments, global chat and private chat notifications etc..
- Table generation by sql syntax in schema.sql
- Connection with database using EntityManager and Spring JPA. No Spring Data for this project.
- Database and file storage code connection

Viewing files:
- Viewing added files on the page and/or by using lightbox on file click for a broader view
- Custom made lightbox for viewing all type of files. Images, videos etc..
- Image compression for better performance. The images on the page are viewed in compressed format. Where the images in the lightbox are downloaded in full quality
- Image compression of the profile photo

Main feed (user posts)
- Adding posts by users with text and/or files
- Deleting posts created by users
- General file view and lightbox file view
- Writing comments under each post
- Deleting user comments
- Feed files are stored to global feed directory

WebSocket/Stomp
- Using STOMP for client-server communication
- User login/logout (viewing logged and offline users)
- Global chat beetwen users
- Private p2p chat between users
- Using STOMP to upload information only to logged clients:  
  1) Uploading added posts in the main feed  
  2) Uploading new comments
  3) Upload messages to global chat
  4) Upload messages to private chate between users  
  5) Viewing which user is online

Online/Offline window
- Viewing which users are online when connecting to stomp or offline on disconnect.
- Opening p2p chat window when user is clicked

Chat
- global chat with all users
- Chat window component for user-to-user private chat (private messages are stored encoded in the database)
- Notification if a message is received by other user


## Endpoints
### ChatDataController
Used to retrieve public chat messages, p2p user messages and pending messages of the authorized user


| Method | URI | Name | Action | Security | Other |
|--------|-----|------|--------|----------|-------|
|  `GET` | `/api/chat` | `findPublicChatMessages` | `Get Chunk of public messages` | `USER` | `Param: limit, offset` |
|  `GET` | `/api/chat/{senderUUID}/{receiverUUID}` | `findPrivateChatMessages` | `Get Chunk of p2p messages` | `USER` | `Param: senderUUID, receiverUUID, limit, offset` |
|  `GET` | `/api/chat/pending-messages` | `getUserData` | `Get pending messages` | `USER` | `Nope` |

### FileController
Used to work with user files and directories. Storing files, creating, deleting folders in user directory,

| Method | URI | Name | Action | Security | Other |
|--------|-----|------|--------|----------|-------|
|  `GET` | `/api/storage` | `getUserDirectories` | `Get array with user folders` | `USER` | `Params:  userUUID` |
|  `GET` | `/api/storage/files` | `getFileData` | `Get Chunk<FileData>` | `USER` | `Params:  userUUID, directory, limit, offset` |
|  `GET` | `/api/storage/file/{userUUID:.+}/{directory:.+}/{fileName:.+}` | `downloadFile` | `Get file in byte[]` | `USER` | `Nope` |
|  `GET` | `/api/storage/image/{id:.+}/{directory:.+}/{fileName:.+}` | `downloadImage` | `Get image in byte[]` | `USER` | `Nope` |
|  `GET` | `/api/storage/profile/high-quality/{userUUID}` | `downloadProfileHighQuality` | `Get profile photo` | `USER` | `Nope` |
|  `GET` | `/api/storage/profile/low-quality/{userUUID}` | `downloadProfileLowQuality` | `Get profile photo` | `USER` | `Nope` |
|  `POST` | `/api/storage` | `uploadFiles` | `Upload array of files to dir` | `USER` | `Body: files[], Params:  directory` |
|  `POST` | `/api/storage/images` | `uploadFilesAndImages` | `Upload array of files to dir` | `USER` | `Body: files[],images[] Params:  directory` |
|  `POST` | `/api/storage/profile` | `uploadProfilePhoto` | `Upload profile photo` | `USER` | `Body: photo` |
|  `POST` | `/api/storage/directory` | `createFolder` | `Create folder in user dir` | `USER` | `Body: directory` |
|  `DELETE` | `/api/storage/{directory}` | `deleteFolder` | `Delete folder from user dir` | `USER` | `Nope` |
|  `DELETE` | `/api/storage/{directory}/{fileName}` | `deleteFile` | `Delete file` | `USER` | `Nope` |
|  `DELETE` | `/api/storage/profile` | `deleteProfilePhoto` | `Delete prophile photo` | `USER` | `Nope` |

### UserController
Get all users or a single user.

| Method | URI | Name | Action | Security | Other |
|--------|-----|------|--------|----------|-------|
|  `GET` | `/api/user` | `getUsers` | `Get list of users` | `USER` | `Nope` |
|  `GET` | `{uuid}` | `getUserByUUID` | `Get a single user` | `USER` | `Nope` |


### AccountController
Account creation and maintenance. 

| Method | URI | Name | Action | Security | Other |
|--------|-----|------|--------|----------|-------|
|  `GET` | `/api/account/email` | `checkIfEmailExists` | `Ret. true if email exist` | `ALL` | `Nope` |
|  `GET` | `/api/account/reset-request` | `resetPasswordRequest` | `Send reset email` | `ALL` | `Param: email` |
|  `PATCH` | `/api/account/reset-password` | `resetPassword` | `Reset password, send it by email` | `ALL` | `Nope` |
|  `PATCH` | `/api/account/password` | `changePassword` | `Change password` | `USER` | `Body: Credentials.class` |
|  `PATCH` | `/api/account/email` | `changeEmail` | `Change email` | `USER` | `Body: MailData.class` |
|  `POST` | `/api/account/register` | `register` | `Register account` | `ALL` | `Body: UserRequestModel.class` |
|  `PATCH` | `/api/account/confirm-account` | `confirmAccount` | `Send verification token` | `ALL` | `Body: tokenUUID` |
|  `DELETE` | `/api/account` | `deleteAccount` | `Delete authorized user account` | `USER` | `Nope` |

### MainFeedController
Adding main feed posts, deleting etc..

| Method | URI | Name | Action | Security | Other |
|--------|-----|------|--------|----------|-------|
|  `GET` | `/api/feed` | `getFeed` | `Get Chunk<FeedModelDto>` | `USER` | `Param: limit, offset` |
|  `GET` | `/api/feed/file/{feedId}/{fileName}` | `downloadFeedFile` | `Get a single added file` | `USER` | `Nope` |
|  `GET` | `/api/feed/image/{feedId}/{fileName}` | `downloadFeedImageFile` | `Get a single image` | `USER` | `Nope` |
|  `POST` | `/api/feed` | `postFeed` | `Add a feed with text only` | `USER` | `Body: text` |
|  `POST` | `/api/feed/addons` | `postFeedWithFiles` | `Add a feed with text and files` | `USER` | `Body: files[], Param: text` |
|  `POST` | `/api/feed/addons-comp` | `postFeedWithFilesPlusCompressed` | `Add a feed with text, files and images` | `USER` | `Body: files[], images[], Param: text` |
|  `DELETE` | `/api/feed/{feedId}` | `deleteFeed` | `Delete feed with it's files and images` | `USER` | `Nope` |

⛔ However user can only delete the feed he created. He can't touch other posts.

### FeedCommentController
Adding and deleting comments, etc.. 

| Method | URI | Name | Action | Security | Other |
|--------|-----|------|--------|----------|-------|
|  `GET` | `/api/feed-comment/{feedId}` | `getFeedComments` | `Get Chunk<FeedComment>` | `USER` | `Param: limit, offset` |
|  `POST` | `/api/feed-comment` | `saveFeedComment` | `Post a comment` | `USER` | `Body: FeedCommentDto` |
|  `DELETE` | `/api/feed-comment/{commentId}` | `deleteFeedComment` | `Delete a comment` | `USER` | `Nope` |

### MailClient
Sending register account mails. Verification mails. Reseting password, etc..

### StompController
Stomp messaging and user login/logout.

This controller fetches the messages from a user and send them to message broker queue's.
