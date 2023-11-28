<a name="readme-top"></a>

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/trongtinh7727/StudentManager/">
    <img src="./logo.png" alt="Logo" width="" height="">
  </a>

  <h3 align="center">IIEX - Student Management </h3>

  <p align="center">
    Excellent Experience
    <br />
    <br />
    <a href="https://github.com/trongtinh7727/StudentManager/">View Demo</a>
    ·
    <a href="https://github.com/trongtinh7727/StudentManager/issues">Report Bug</a>
    ·
    <a href="https://github.com/trongtinh7727/StudentManager/issues">Request Feature</a>
  </p>
</div>

<!--   -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li><a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#project-overview">Project Overview</a></li>
        <li><a href="#user-accounts">User Accounts</a></li>
        <li><a href="#file-import-template">File Import Template</a></li>
        <li><a href="#key-software-functions">Key Software Functions</a></li>
        <li><a href="#user-roles">User Roles</a></li>
      </ul>
    </li>
    <li><a href="#references">References</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
# About The Project

## Project Overview
This project is a comprehensive study of Google Firebase Firestore with the objective of developing a real-time app for student information management. The app will provide various functionalities related to user account management, student management, and data import/export.

**Compatibility:** This project is designed to work with Android 11 and above.
## User Accounts
The app includes the following user accounts for testing:
- **Admin:** admin@app.com - admin123
- **Manager:** manager@app.com - admin123
- **Employee:** employee@app.com - admin123

## File Import Template
To successfully import student data into your system, follow these instructions and use the provided template. Additionally, you can explore the sample files `certificates_import_sample.csv`  and `student_import_sample.csv` for guidance.
### Importing Student Data

You can use the following template for importing student data:

`Email,FullName,FacultyCode,ClassRoom,CertificateCode1,CertificateName1,CertificateCode2,CertificateName2,...`

**Faculty Codes:**
- 101: Công nghệ thông tin
- 102: Dược
- 103: Điện - Điện tử
- 104: Kế toán
- 105: Khoa học thể thao
- 106: Khoa học ứng dụng
- 107: Toán - Thống kê
- 108: Kỹ thuật công trình
- 109: Luật
- 110: Mỹ thuật công nghiệp
- 111: Tài chính - Ngân hàng
- 112: Lao động và Công đoàn
- 113: Ngoại ngữ
- 114: Quản trị kinh doanh

Example:
```csv
student1@app.com,Phạm Trung Linh,102,210102,A1,English CAM,B1,Chine CAM
student10@app.com,Vũ Hữu Khoa,102,210102
student23@app.com,Phạm Hữu Bảo,102,210102
```

### Importing Certificates
You can use the following template for importing certificates:

`CertificateCode,CertificateName`

**Example:**
```csv
A1,English Proficiency
A2,English Mastery
```

## Key Software Functions
### User Account Management
- **User Login:** All users can log in using their credentials.
- **Change Profile Picture:** Users can update their profile pictures.
- **View System Users:** Users can view the list of system users.
- **Add New User:** Admins can add new users with details such as name, age, phone number, and status (normal/locked).
- **Delete User:** Admins can delete user accounts.
- **Modify User Information:** Admins can update user information.
- **View Login History:** Users can view their login history.

### Student Management
- **View List of Students:** Users can view the list of students.
- **Add New Student:** Admins and managers can add new students.
- **Delete Student:** Admins and managers can delete student records.
- **Update Student Information:** Admins and managers can update student information.
- **Sort Students:** Users can sort the list of students based on various criteria.
- **Search for Students:** Users can search for students using multiple criteria.
- **Access Student Details:** Users can view complete student information and manage a list of certificates for students.

### Data Import/Export
- **Import Students:** Users can import a list of students from a file.
- **Export Students:** Users can export the list of students to Excel/CSV.
- **Import Certificates:** Users can import a list of certificates for a student from a file.
- **Export Certificates:** Users can export the list of certificates to Excel/CSV.

## User Roles
The app provides three user roles: admin, manager, and employee.
- **Admin:** Admin accounts are integrated with the system and have full access to all functions.
- **Manager:** Managers can perform all functions related to students.
- **Employee:** Employees can only view content and update their profile pictures. They cannot edit other content.

This project serves as the final report for the `Mobile` course, providing a comprehensive exploration of Firebase Firestore and its application in student information management.


<p align="right">(<a href="#readme-top">back to top</a>)</p>



# References:
    
**MVVM:**
- [MVVM Architecture in Android Development (English)](https://blog.devgenius.io/a-guide-on-mvvm-architecture-in-android-development-3906a6c9bfc8)
- [Mô hình MVVM và cách triển khai trong ứng dụng Android (Vietnamese)](https://viblo.asia/p/mo-hinh-mvvm-va-cach-trien-khai-trong-ung-dung-android-LzD5dREOZjY)

**ViewModel:**
- [ViewModel - Android Developers (Vietnamese)](https://developer.android.com/topic/libraries/architecture/viewmodel?hl=vi)

**LiveData:**
- [LiveData - Android Developers (Vietnamese)](https://developer.android.com/topic/libraries/architecture/livedata?hl=vi)

**Firebase:**
- [Firebase Firestore Documentation](https://firebase.google.com/docs/firestore)
- [Firebase Authentication Documentation](https://firebase.google.com/docs/auth)
- [Firebase Storage Documentation](https://firebase.google.com/docs/storage)

**Firebase Admin SDK:**
- [Firebase Admin SDK Setup](https://firebase.google.com/docs/admin/setup)
- [Firebase Admin SDK User Management](https://firebase.google.com/docs/auth/admin/manage-users)
- [Firebase Admin SDK User Management Example](https://github.com/padejar/firebase-admin-sdk-user-management)

**Glide:**
- [Glide Documentation](https://bumptech.github.io/glide/)

<p align="right">(<a href="#readme-top">back to top</a>)</p>