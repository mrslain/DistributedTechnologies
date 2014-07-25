1. Ожидается, что в ходные данные располагаются в hdfs в директории input-data/input
Директория может быть проинициализированна из первой лабы про Hadoop (Hadoop/data/input)

```bash
hadoop fs -put Hadoop/data/input input-data/input
```

2. Резльтат работы будет в hdfs в output-data/grammCountOutput/Monogramm (Bigramm, Trigramm) для монограмм (биграмм, триграмм)

3. Положить workflow application в hdfs

```bash
hadoop fs -put oozie/grammCount
```

4. Запуск:

```bash
oozie job -oozie http://localhost:11000/oozie -config oozie/grammCount/job.properties -run
```
