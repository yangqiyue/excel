# excel
一个简单的基于poi，封装的对象注解式的导入导出。
# 介绍
此demo适合初学者学习，如果有项目开发需要的，请自行根据业务进行修改。如果有幸哪个大神看到，请多指点指点，不胜感激！
# 涉及知识点
1、poi-excel 基本操作（工具） 
2、自定义注解的使用 
3、全局异常捕获的定义 
4、jkd1.8新特性：Lambda 表达式 、函数式接口 等等 
5、统一出参包装类 
6、lombok
...
比较适合初学者~

## 2019-10-15 更新版本
1、实现金额单位的转换。
2、解决excel2016以上单元格式异常问题。
3、解决前端获取excel的文件名乱码。
（目前excel的名字只能通过前端获取，转码之后才能显示中文，原因在于前端可以将excel操作的登录、权限放在头文件里面。而非调起新的页面。）
4、cellStyle 创建过多问题。
