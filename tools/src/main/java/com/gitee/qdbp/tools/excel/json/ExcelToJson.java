package com.gitee.qdbp.tools.excel.json;

/**
 * Excel数据转换为JSON格式数据
 *
 * @author zhaohuihua
 * @version 181027
 */
public class ExcelToJson {

    // user.xlsx[MainSheet]
    //     id name   gender
    //     1  jack   male
    // user.xlsx[AddressSheet]
    //     id name   city     details
    //     1  home   hefei    xxxxx
    //     1  office nanjing  yyyyy
    // user.xlsx[ExtraSheet]
    //     id intro      description
    //     1  jack-intro jack-description
    // field:users, excel:user.xlsx[MainSheet], id:1, header:1, join: [{ type:json, excel:[AddressSheet], id:1, key:2, header:1 }]
    // --- users:[ { id:1, name:jack, home:{ city:hefei, details:xxxxx }, office:{ city:nanjing, details:yyyyy } } ]
    // field:users, excel:user.xlsx[MainSheet], id:1, header:1, join: [{ field:address, type:list, excel:[AddressSheet], id:1, header:1 }]
    // --- users:[ { id:1, name:jack, address:[{ name:home, city:hefei, details:xxxxx }, { name:office, city:nanjing, details:yyyyy }] } ]
    // field:users, excel:user.xlsx[MainSheet], id:1, header:1, join: [{ field:address, type:json, excel:[AddressSheet], id:1, header:1 }]
    // --- users:[ { id:1, name:jack, address:{ name:office, city:nanjing, details:yyyyy } } ] // home数据被office覆盖了
    // field:users, excel:user.xlsx[MainSheet], id:1, header:1, join: [{ type:field, excel:[ExtraSheet], id:1, header:1 }]
    // --- users:[ { id:1, name:jack, intro:jack-intro, description:jack-description } ]

}
