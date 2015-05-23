;==================================================================
;Project Title:    BloodCore 
;Author:     	   (c) 2015 ObelardO
;Email:            obelardos@gmail.com
;Version:          Alpha #7
;Date:             18.05.15
;Notes:            Модуль спавнеров
;==================================================================

;Параметры спавнеров
Const SpawnerTime = 1000

;Переменная кол-ва спавнеров
Global SpanwerCount

;Структура спавнера
Type tSpawner
	Field Entity
	Field Enable
	Field Time
	Field ID
End Type

;Добавление спавнеров
Function CreateSpawner(Entity)
	Spawner.tSpawner = New tSpawner
	Spawner\Entity   = Entity
	Spawner\ID       = SpanwerCount
	Spawner\Enable   = True

	xEntityAlpha Spawner\Entity, 0.0
	SpanwerCount = SpanwerCount + 1
End Function
