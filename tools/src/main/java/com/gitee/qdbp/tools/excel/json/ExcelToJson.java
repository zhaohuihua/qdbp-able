package com.gitee.qdbp.tools.excel.json;

/**
 * Excel数据转换为JSON格式数据<br>
 * <pre>
 * <b>Excel数据</b>
 * user.xlsx[MainSheet]
 *     id name   gender
 *     1  jack   male
 * user.xlsx[AddressSheet]
 *     id name   city     details
 *     1  home   hefei    xxxxx
 *     1  office nanjing  yyyyy
 * user.xlsx[ExtraSheet]
 *     id intro      description
 *     1  jack-intro jack-description
 * 
 * <b>ToJsonMetadata</b> = { fieldName:users, fileName:user.xlsx, sheetName:MainSheet, idColun:1, headerRows:1 }
 * 
 * <b>MergeToJson</b>, 一对多合并, 将子数据以keyColumn指定列的字段内容作为字段名合并至主数据, 
 *     如下示例的主数据多了home/office两个字段
 *     MergeToJson = { keyColumn:2, sheetName:AddressSheet, idColumn:1, headerRows:1 }
 *     { id:1, name:jack, home:{ city:hefei, details:xxxxx }, office:{ city:nanjing, details:yyyyy } }
 * 
 * <b>MergeToJson</b>, 一对一合并, 未指定keyColumn而是指定了fieldName, 则将子数据列表以fieldName指定的字段名合并至主数据, 
 *     如果子数据有多条后出现的会覆盖前面的, 如下示例的主数据多了address字段, home数据被office覆盖了
 *     MergeToJson = { fieldName:address, sheetName:AddressSheet, idColumn:1, headerRows:1 }
 *     { id:1, name:jack, address:{ name:office, city:nanjing, details:yyyyy } }
*
 * <b>MergeToList</b>, 一对多合并, 将子数据列表以fieldName指定的字段名合并至主数据, 
 *     如下示例的主数据多了address字段, 内容为子数据列表
 *     MergeToJson = { fieldName:address, sheetName:AddressSheet, idColumn:1, headerRows:1 }
 *     { id:1, name:jack, address:[{ name:home, city:hefei, details:xxxxx }, { name:office, city:nanjing, details:yyyyy }] }
 * 
 * <b>MergeToField</b>, 一对一合并, 将子数据所有字段合并至主数据, 如下示例的主数据会具有子数据的所有字段
 *     MergeToField = { sheetName:ExtraShee, idColumn:1, headerRows:1 }
 *     { id:1, name:jack, intro:jack-intro, description:jack-description }
 * </pre>
 *
 * @author zhaohuihua
 * @version 181027
 */
public class ExcelToJson {

}
