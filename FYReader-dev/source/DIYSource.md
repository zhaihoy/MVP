[toc]

## 熊猫读书 DIY书源说明

[参考书源](../app/src/main/assets/ReferenceSources.json)

### 一、解析器

#### 1、Matcher

* 说明：Matcher解析器原理为取中间文本，语法最为简单，专门为小白设计，支持html和json数据

* 语法：左边文本+\<text\>/\<html\>+右边文本(+##+函数)，通配符(*)，[函数说明](#fun)

  * 其中\<text\>以纯文本形式获取，\<html\>以html形式解析成纯文本

  * 例如：

    ```html
    <a href="https://www.37zww.net/1/1812/">斗罗大陆IV终极斗罗</a>
    ```

    取书名：```<a href="(*)"><text></a>```------> ```斗罗大陆IV终极斗罗```

    取链接：```<a href="<text>">(*)</a>```------> ```https://www.37zww.net/1/1812/```

#### 2、Xpath

* 说明：实现库[hegexiaohuozi/JsoupXpath](https://github.com/zhegexiaohuozi/JsoupXpath)，仅支持html数据
* 语法：Xpath语法(+##+函数)，[函数说明](#fun)
* 教程 ：[XPath教程-入门](https://www.w3school.com.cn/xpath/index.asp)、[XPath教程-基础](https://zhuanlan.zhihu.com/p/29436838)、[XPath教程-高级](https://zhuanlan.zhihu.com/p/32187820)、[XPath库的说明](https://github.com/zhegexiaohuozi/JsoupXpath/blob/master/README.md)

#### 3、JsonPath

* 说明：实现库[json-path/JsonPath](https://github.com/json-path/JsonPath)，仅支持json数据
* 语法：JsonPath语法(+##+函数)，[函数说明](#fun)
* 教程：[JsonPath教程](https://blog.csdn.net/koflance/article/details/63262484)



### <span id="fun">二、函数说明</span>

#### 1、概要

* 书源规则支持四个普通函数和一个列表函数，分别为@r/@replace，@a/@append，@c/contains，@nc/notContains，!，%
* 如何使用：在书源规则后添加 ***##+函数*** ，多个函数以半角分号 **;** 分隔，若函数内部出现 **;** 请使用 **\;** 转义

#### 2、@r/@replace替换函数

* 语法：@r/@replace(oldStr,newStr)

* oldStr为替换前字符串(支持正则)，newStr为替换后的字符串，中间以半角逗号分隔，无需加引号

* 若替换字符串中含有逗号 **,**请使用 **\,** 转义

* 例如：

  * ```html
    <a href="https://www.37zww.net/1/1812/">斗罗大陆IV终极斗罗</a>
    ```

    取书名并将IV替换为4：```<a href="(*)"><text></a>##@r(IV,4)```------> ```斗罗大陆4终极斗罗```

#### 3、@a/@append拼接函数

* 语法：@a/@append(str1+str2+...+strn)

* str1-n为任意字符串，无需加引号，各字符串以加号 **+ ** 分隔

* 若字符串中含有加号 **+** 请使用 **\+** 转义

* 若要拼接规则获取的字符串，请使用\<text\>/\<html\>

* 例如：

  * ```html
    <a href="/1/1812/">斗罗大陆IV终极斗罗</a>
    ```

    取书籍链接并拼接域名：```<a href="<text>">(*)</a>##@a(https://www.37zww.net+<text>)```

    ------> ```https://www.37zww.net/1/1812/```

#### 4、@c/contains包含函数

* 语法@c/contains(str)

* str为任意字符串，无需加引号

* 执行结果：如果不包含str，则不获取

* 例如：

  * ```html
    <a href="https://www.37zww.net/1/1812/">斗罗大陆IV终极斗罗</a>
    <a href="https://www.37zww.net/0/730/">斗罗大陆III龙王传说</a>
    <a href="https://www.37zww.net/2/2509/">斗破苍穹</a>
    ```

  * 取包含斗罗大陆的书名：```<a href="(*)"><text></a>##@c(斗罗大陆)```

    ------> ```[斗罗大陆IV终极斗罗, 斗罗大陆III龙王传说]```

#### 5、@nc/notContains不包含函数

* 语法@nc/notContains(str)

* str为任意字符串，无需加引号

* 执行结果：如果包含str，则不获取

* 例如：

  * ```html
    <a href="https://www.37zww.net/1/1812/">斗罗大陆IV终极斗罗</a>
    <a href="https://www.37zww.net/0/730/">斗罗大陆III龙王传说</a>
    <a href="https://www.37zww.net/2/2509/">斗破苍穹</a>
    ```

  * 取不包含斗罗大陆的书名：```<a href="(*)"><text></a>##@nc(斗罗大陆)```

    ------> ```斗破苍穹```

#### 6、!跳过列表前几个函数

* 语法：!n
* n为跳过数量
* 此函数仅支持书籍列表(Xpath、JsonPath)，章节列表(Matcher、Xpath、JsonPath)

#### 7、!列表分组反转函数

* 语法：%n
* 以n为分组长度进行分组，并对各组进行反转
* 此函数仅支持书籍列表(Xpath、JsonPath)，章节列表(Matcher、Xpath、JsonPath)
* 例如：
    * ```html
        <a href="3.html">第三章</a>
        <a href="2.html">第二章</a>
        <a href="1.html">第一章</a>
      
        <a href="6.html">第六章</a>
        <a href="5.html">第五章</a>
        <a href="4.html">第四章</a>
      
        <a href="9.html">第九章</a>
        <a href="8.html">第八章</a>
        <a href="7.html">第七章</a>
      
        <a href="10.html">第十章</a>
      ```
    
    * 以上列表每三个为一组顺序倒置了，故可用此函数：%3 即可的到正确排序的列表



### 三、书源编辑

#### 1、书源基本信息

* 1）解析器：默认为Xpath，可根据需要选择，目前一个书源仅支持一个解析器
* 2）书源URL：书源唯一标识，不能为空
* 3）书源名称：不能为空
* 4）书源分组：不同分组以;隔开
* 5）书源字符编码：默认UTF-8
* 6）书源说明：这是您留给使用者的说明

#### 2、搜索规则

* 1）搜索地址：搜索关键词以{key}进行占位;post请求以“,”分隔url,“,”前是搜索地址,“,”后是请求体
* 2）搜索字符编码：默认使用书源字符编码
* 3）书籍列表规则：
  * 对于Mathcer解析器：此处填写书籍列表所在区间，支持普通函数；
  * 对于Xpath/JsonPath解析器：此处填写书籍列表规则，不支持普通函数，规则后接##!加数字可以跳过列表前几个
* ...书名、作者、分类、简介、连载状态、字数、最新章节、更新时间、封面、目录URL...
* 14）详情也URL规则：为空时使用目录URL
* 15）关联书籍详情：搜索时是否关联书籍详情页，填true/t表示关联，false/f表示不关联，测试时不生效

#### 3、详情规则

* 书名、作者、分类、简介、连载状态、字数、最新章节、更新时间、封面、目录URL

#### 4、目录规则

* 1）章节BaseURL规则：如果章节URL(一般为相对路径)无法定位章节，可填写此规则获取，默认为书源URL
* 2）目录列表规则：
  * 对于Mathcer解析器：此处填写书籍列表所在区间，支持普通函数；
  * 对于Xpath/JsonPath解析器：此处填写书籍列表规则，不支持普通函数，规则后接##!加数字可以跳过列表前几个
* 3）章节名称(和URL)规则：
  * 对于Mathcer解析器：此处填写章节名称和URL规则，其中章节名称以\<title\>占位，章节URL以\<link\>占位，不支持普通函数，规则后接##!加数字可以跳过列表前几个
    * 例如：```<dd><a href="<link>"><title></a></dd>```
  * 对于Xpath/JsonPath解析器：此处填写章节名称，支持普通函数

* 4）章节URL规则：
  * 对于Mathcer解析器：此处不用填写
  * 对于Xpath/JsonPath解析器：此处填写章节URL规则
* 5）目录下一页URL规则：填写后获取目录时将会不断地从目录下一页获取章节，直至下一页URL为空时停止，注意：千万不要获取恒存在的URL，否则将出现死循环甚至崩溃

#### 5、正文规则

* 1）正文规则
* 2）正文BaseURL规则：如果下一页URL(一般为相对路径)无法定位下一页，可填写此规则获取，默认为书源URL
* 3）正文下一页URL规则：填写后正文时将会不断地从下一页获取内容，直至下一页URL为空时停止，注意：千万不要获取恒存在的URL，否则将出现死循环甚至崩溃

#### 6、URL说明

* 1）所有URL均支持相对路径
* 2）如当前规则中获取了BaseURL，则软件将会以此BaseURL获取URL的绝对路径，否则将以书源URL获取URL的绝对路径



### 四、书源测试

#### 1、概要

* 书源测试分为测试搜索配置、测试书籍详情、测试目录列表、测试章节内容
* 测试界面可查看解析结果和网页源码
  * 解析结果以Json格式输出
  * 网页源码高亮显示

#### 2、测试搜索配置

* 在输入关键词确认后将会跳转至测试界面
* 解析结果分为信息和结果
  * 信息中输出解析书籍数量
  * 结果中输出书籍列表

#### 3、测试书籍详情

* 在输入书籍URL确认后将会跳转至测试界面

* 解析结果为单本书籍信息

#### 4、测试目录列表

* 在输入目录URL确认后将会跳转至测试界面
* 解析结果分为信息和结果
  * 信息中输出解析章节数量
  * 结果中输出章节列表

#### 5、测试章节内容

* 在输入章节URL确认后将会跳转至测试界面
* 解析结果为章节内容