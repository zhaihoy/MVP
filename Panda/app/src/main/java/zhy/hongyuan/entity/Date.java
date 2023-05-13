/*
 * This file is part of panda.
 * panda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * panda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with panda.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2020 - 2022 fengyuecanzhu
 */

package zhy.hongyuan.entity;


import zhy.hongyuan.util.help.CalendarHelper;

import java.io.Serializable;



public class Date implements Serializable {

    private static final long serialVersionUID = -1251952358800941760L;

    private int year; //年
    private int month; //月
    private int date; //日
    private int day; //星期


    private boolean festival;



    public Date(){


    }

    public int compare(Date date){
        if(year == date.getYear() && month == date.getMonth() && this.date == date.getDate()){
            return 0;
        }else if(year > date.getYear()
                || (year == date.getYear() && month > date.getMonth())
                ||(year == date.getYear() && month == date.getMonth() && this.date > date.getDate())){
            return 1;
        }else if(year < date.getYear()
                || (year == date.getYear() && month < date.getMonth())
                ||(year == date.getYear() && month == date.getMonth() && this.date < date.getDate())){
            return -1;
        }else {
            return -101;
        }
    }

    public boolean isSameDate(Date date){
        return year == date.getYear() && month == date.getMonth() && this.date == date.getDate();
    }



    public Date copyDate(){
        Date resDate = new Date();
        resDate.setYear(year);
        resDate.setMonth(month);
        resDate.setDate(this.date);
        resDate.setDay(day);
        return resDate;
    }

    public void lastMonth(){
        if(month == 1){
            year--;
            month  = 12;
        }else {
            month--;
        }
    }

    public void nextMonth(){
        if(month == 12){
            year++;
            month = 1;
        }else {
            month++;
        }
    }

    public Date lastDate(){
        Date date = copyDate();
        if(date.getDate() == 1){
            if(month == 1){
                date.setMonth(12);
                date.setYear(date.getYear() - 1);
            }else {
                date.setMonth(date.getMonth() - 1);
            }
            date.setDate(CalendarHelper.getMonthDays(date.getYear(),date.getMonth()));
        }else {
            date.setDate(date.getDate() - 1);
        }
        if(date.getDay() == 0){
            date.setDay(6);
        }else {
            date.setDay(date.getDay() - 1);
        }
        return date;
    }

    public Date nextDate(){
        Date date = copyDate();
        if(date.getDate() == CalendarHelper.getMonthDays(date.getYear(),date.getMonth())){
            if(month == 12){
                date.setMonth(1);
                date.setYear(date.getYear() + 1);
            }else {
                date.setMonth(date.getMonth() + 1);
            }
            date.setDate(1);
        }else {
            date.setDate(date.getDate() + 1);
        }
        if(date.getDay() == 6){
            date.setDay(0);
        }else {
            date.setDay(date.getDay() + 1);
        }
        return date;
    }

    public long toTime(){
        java.util.Date date1 = new java.util.Date(year-1900,month-1,date);
        return date1.getTime();
    }



    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getDate() {
        return date;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public boolean isFestival() {
        return festival;
    }

    public void setFestival(boolean festival) {
        this.festival = festival;
    }
}
