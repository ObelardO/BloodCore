;==================================================================
;Project Title:    BloodCore 
;Author:     	   (c) 2015 ObelardO
;Email:            obelardos@gmail.com
;Version:          Alpha #7
;Date:             18.05.15
;Notes:            Модуль ракет
;==================================================================

;Тип коллизии ракет
Const CollType_Rocket = 6

;Параметры ракеты
Const RocketLiveTime = 3000
Const RocketSpeed  = 2
Const RocketRadius = 3

;Модель ракеты
Global RocketModel

;Структура ракеты
Type tRocket
	Field Entity
	Field BurnTime
End Type

;Инициализация модуля ракет
Function InitRocket()
	RocketModel = xLoadMesh("base\models\Rocketitem\item.b3d")
	xHideEntity RocketModel
End Function

;Добавление ракеты
Function CreateRocket(RocketX#, RocketZ#)
	Rocket.tRocket = New tRocket

	Rocket\Entity = xCopyEntity(RocketModel)

	RocketFire = xCreateSprite(Rocket\Entity)
	xEntityTexture RocketFire, FlashTexture, 3
	xEntityFX      RocketFire, FX_FULLBRIGHT
	xScaleSprite   RocketFire, 0.5, 0.5
	
	Rocket\BurnTime = GameMillis + RocketLiveTime
	xPositionEntity Rocket\Entity, PlayerX, 2.5, PlayerZ
	xRotateEntity   Rocket\Entity, PlayerPitch, PlayerYaw, 0
	xMoveEntity     Rocket\Entity, RocketX, 0, RocketZ
	xEntityType     Rocket\Entity, CollType_Rocket
End Function

;Обновление ракет
Function UpdateRockets()
	For Rocket.tRocket = Each tRocket
		Entity = xEntityCollided(Rocket\Entity, CollType_Level)
		If Entity Rocket\BurnTime = GameMillis
	
		If Rocket\BurnTime <= GameMillis
			CreateExplosion(xEntityX(Rocket\Entity), xEntityY(Rocket\Entity), xEntityZ(Rocket\Entity))
			CreateSoot(xEntityX(Rocket\Entity), xEntityZ(Rocket\Entity))

			xFreeEntity Rocket\Entity
			Delete Rocket
		Else
			xMoveEntity Rocket\Entity, 0, 0, RocketSpeed
		End If
	Next
End Function

;Сброс модуля ракет
Function ResetRockets()
	For Rocket.tRocket = Each tRocket
		xFreeEntity Rocket\Entity
		Delete Rocket
	Next
End Function 
