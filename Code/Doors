;==================================================================
;Project Title:    BloodCore 
;Author:     	   (c) 2015 ObelardO
;Email:            obelardos@gmail.com
;Version:          Alpha #7
;Date:             18.05.15
;Notes:            Моудль дверей
;==================================================================

;Режимы дверей
Const DoorStatusClosed  = 0
Const DoorStatusOpening = 1
Const DoorStatusOpened  = 2
Const DoorStatusClosing = 3

;Высота двери
Const DoorHeight = 19

;Звук двери
Global DoorSound

;Структура двери
Type tDoor
	Field Entity
	Field Status
	Field Displace#
End Type

;Инициализация модуля дверей
Function InitDoors()
	DoorSound = xLoad3DSound("base\sounds\door\Door.wav")
End Function

;Добавление двери
Function CreateDoor(Entity)
	Door.tDoor = New tDoor
	Door\Entity = Entity
End Function

;Обновление дверей
Function UpdateDoors()
	For Door.tDoor = Each tDoor
		Select Door\Status
			;Открытие дверей
			Case DoorStatusOpening
				If Door\Displace < DoorHeight
					Door\Displace = Door\Displace + 0.15
					xMoveEntity Door\Entity, 0, 0.15, 0
				Else
					Door\Status = DoorStatusOpened
				End If
			;Закрытие дверей
			Case DoorStatusClosing
				If Door\Displace > 0
					Door\Displace = Door\Displace - 0.15
					xMoveEntity Door\Entity, 0,-0.15, 0
				Else
					Door\Status = DoorStatusClosed
				End If
		End Select
	Next
End Function

;Открыть все двери
Function OpenDoors()
	For Door.tDoor = Each tDoor
		If Door\Status <> DoorStatusOpening
		Door\Status = DoorStatusOpening
		xEmitSound(DoorSound, Door\Entity)
		End If
	Next
End Function

;Закрыть все двери
Function CloseDoors()
	For Door.tDoor = Each tDoor
		If Door\Status <> DoorStatusClosed
		Door\Status = DoorStatusClosing
		xEmitSound(DoorSound, Door\Entity)
		End If 
	Next
End Function

;Сброс параметров дверей
Function ResetDoors()
	CloseDoors()
End Function



