<!DOCTYPE html>
<html>
<head>
    <title>ELIMAUTO</title>
</head>
<body>
<h1>ELIMAUTO</h1><hr>
<h4>Подробная информация об объявлении</h4>
<#list images as img>
    <img src="/images/${img.id}" height="60px"/><br><br>
</#list>
<b>Название объявления: </b>${announcement.title}<br>
<b>Описание объявления: </b>${announcement.description}<br>
<b>Цена: </b>${announcement.price}<br>
<b>Город: </b>${announcement.city}<br>
<b>Автор: </b><#if announcement.author?has_content>
    ${announcement.author}
    <#else>
    Автор неизвестен
</#if><br>
<hr>
<form action="/announcement/delete/${announcement.id}" method="post">
    <input type="submit" value="Удалить объявление"/>
</form>
</body>
</html>