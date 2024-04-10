# java-kanban
Это бэкенд трекера задач, который позволяет эффективно организовать совместную работу над задачами. 

**Технологический стек: Java, JSON, JUnit, HttpClient, HttpServer, API**

Трекер работает с эпиками (большие задачи, которые могут иметь подзадачи), обычными задачами и подзадачами. В приложении реализованы следующие функции:
* Хранение всех типов задач
* Обновление всех типов задач
* Удаление всех типов задач
* Получение по идентификатору каждого из типа задач
* Удаление по идентификатору каждого из типа задач
* Получение списка всех задач
* Отображение последних просмотренных пользователем задач
Так же реализованы дополнительные методы:
* Получение списка всех подзадач определённого эпика.
* Управление статусами задач (для эпиков вычисляется в зависимости от статусов подзадач)

Проект развивался от хранения данных по задачам и истории просмотра задач в памяти, затем в файле и, наконец, на сервере.

