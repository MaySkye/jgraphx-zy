package com.mxgraph.examples.swing.post;

import cn.edu.subpub.SendWSNCommandWSSyn;

import java.sql.Timestamp;

/**
 * @Author:zhoayi
 * @Description:
 * @Data: Created in 9:46 2019/1/21
 * @Modify By:
 */
public class PostCommand {
    public static void main(String[] args) {
        SendWSNCommandWSSyn commandWSSyn =
                new SendWSNCommandWSSyn("http://10.108.166.14:9066/wsn-subscribe",
                        "http://10.108.166.14:9010/wsn-core");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        /*commandWSSyn.notify("all:Test2", "insert into telemetry(timestamp, device_address, data_name, data_type, detected_value)" +
                " values ('"+timestamp +"','010000000000000101000001','input_power','double','10.6')", true);*/


        commandWSSyn.notify("all:Test2",
                "<fix>update</fix>" +
                        "<content>" +
                        "<DEVICE_ADDRESS>010000000000000101000001</DEVICE_ADDRESS>" +
                        "<DATA_NAME>work_temperature</DATA_NAME>" +
                        "<ORIGINAL_VALUE>25.0</ORIGINAL_VALUE>" +
                        "<PRESENT_VALUE>15  .0</PRESENT_VALUE>" +
                        "<TIME>"+timestamp +"</TIME>" +
                        "</content>", true);
    }
}
