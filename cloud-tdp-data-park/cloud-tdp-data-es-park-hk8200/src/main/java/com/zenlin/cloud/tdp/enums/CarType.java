package com.zenlin.cloud.tdp.enums;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/23  9:57.
 */

public enum CarType {
    QT("其它车型", 1),
    XXQC("小型汽车", 2),
    DXQC("大型汽车", 3),
    ELC("二轮车", 4),
    SLCX("三轮车", 5),
    XR("行人", 6),
    MBC("面包车 ", 7),
    JC("轿车", 8),
    KC("客车", 9),
    HC("货车", 10),
    XHC("小货车", 11),
    SUV("SUV/MPV", 12),
    ZXKC("中型客", 13);
    // 成员变量
    private String plateType;
    private int number;

    //构造方法
    CarType(String plateType, int number) {
        this.number = number;
        this.plateType = plateType;
    }

    // 普通方法
    public static String getType(int number) {
        for (CarType c : CarType.values()) {
            if (c.getNumber() == number) {
                return c.plateType;
            }
        }
        return null;
    }

    public String getPlateType() {
        return plateType;
    }

    public void setPlateType(String plateType) {
        this.plateType = plateType;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
