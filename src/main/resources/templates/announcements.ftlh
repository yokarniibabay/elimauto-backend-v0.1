<!DOCTYPE html>
<html>
<head>
    <title>ELIMAUTO</title>
</head>
<body>
<h1>ELIMAUTO</h1><hr>
    <h4>Объявления по всему Казахстану</h4>
    <form action="/" method="get">
        Поиск по названию объявления: <input type="text" name="title"><br>
        <input type="submit" value="Поиск"/>
    </form>
<#list announcements as announcement>
    <div>
        <p><b>${announcement.title}</b> ${announcement.price} | <a href="/announcement/${announcement.id}">Подробнее...</a></p>
    </div>
    <#else>
    <h3>Здесь пока пусто...</h3>
</#list>
<hr>
<h3>Создать новое объявление</h3>
<form action="/announcement/create" method="post">
    Название объявления: <input type="text" name="title"/><br><br>
    Описание объявления: <input type="text" name="description"/><br><br>
    Цена: <input type="number" name="price"/><br><br>
    Город: <input type="text" name="city"/><br><br>
    Имя: <input type="text" name="author"/><br><br>
<input type="submit" value="Добавить объявление"/>

</form>
</body>
</html>