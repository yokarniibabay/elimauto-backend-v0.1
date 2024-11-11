<!DOCTYPE html>
<html>
<head>
    <title>ELIMAUTO</title>
</head>
<body>
<h1>ELIMAUTO</h1><hr>
    Объявления по всему Казахстану
<#list announcements as announcement>
    <div>
        <p><b>${announcement.title}</b> ${announcement.price} тенге | <a href="/announcement/${announcement.id}">Подробнее...</a></p>
    </div>
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