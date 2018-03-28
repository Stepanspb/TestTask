# TestTask

Имеется очередь элементов на обработку. Каждый элемент имеет собственный идентификатор (itemId) и принадлежит к некоторой группе (groupId). Внутри группы элементы должны обрабатываться строго последовательно, в порядке увеличения идентификаторов элементов. Элементы разных групп могут обрабатываться параллельно. Обработка элемента производится путем вызова некоторого метода с параметрами itemId и groupId, который печатает полученные идентификаторы элементов. Элементы в очередь добавляются асинхронно внешним процессом. После обработки элемент должен быть удален из очереди. 

Написать обработчик очереди, работающий в несколько потоков. Максимальное количество потоков ограничено, задается при старте обработчика и в общем случае меньше числа групп. Обеспечить равномерную обработку групп элементов: наличие в очереди групп с большим количеством элементов не должно приводить к длительным задержкам в обработке других групп.
