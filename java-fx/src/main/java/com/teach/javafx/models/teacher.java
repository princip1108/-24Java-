// Teacher.java
package com.teach.javafx.models;

import java.time.LocalDate;

public class teacher {
    private String num;          // 工号
    private String name;        // 姓名
    private String dept;       // 院系
    private String title;       // 职称
    private String degree;      // 学位
    private String card;        // 证件号码
    private String gender;      // 性别
    private LocalDate birthday; // 出生日期
    private String email;       // 邮箱
    private String phone;      // 电话
    private String address;    // 地址
    private String photo;      // 照片路径或Base64编码

    // 构造函数
    public teacher() {
    }

    public teacher(String num, String name, String dept, String title, String degree,
                   String card, String gender, LocalDate birthday, String email,
                   String phone, String address, String photo) {
        this.num = num;
        this.name = name;
        this.dept = dept;
        this.title = title;
        this.degree = degree;
        this.card = card;
        this.gender = gender;
        this.birthday = birthday;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.photo = photo;
    }

    // Getter和Setter方法
    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "num='" + num + '\'' +
                ", name='" + name + '\'' +
                ", dept='" + dept + '\'' +
                ", title='" + title + '\'' +
                ", degree='" + degree + '\'' +
                ", card='" + card + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday=" + birthday +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}