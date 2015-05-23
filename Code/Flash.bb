;==================================================================
;Project Title:    BloodCore 
;Author:     	   (c) 2015 ObelardO
;Email:            obelardos@gmail.com
;Version:          Alpha #7
;Date:             18.05.15
;Notes:            Модуль вспышек
;==================================================================

;Константы для вспышек
Const FlashCount = 5
Const LiveTime = 5

;Текстура вспышки
Global FlashTexture

;Структура вспышки
Type tFlash
	Field Entity
	Field Point
	Field BurnTime
End Type

;Инициализация модуля вспышек
Function InitFlashes()
	FlashTexture = xLoadAnimTexture("base\textures\gunfire.png", FLAGS_ALPHA, 128, 128, 0, FlashCount + 1)
End Function

;Добавление вспышки
Function CreateFlash(FlashX#, FlashZ#, Entity, FlashY = 0)
	Flash.tFlash = New tFlash
	Flash\BurnTime = GameMillis + LiveTime

	Flash\Entity = xCreateSprite(Entity)
	xMoveEntity Flash\Entity, FlashX, -0.2 + FlashY, FlashZ

	xEntityTexture  Flash\Entity, FlashTexture, Rand(0, FlashCount)
	xScaleSprite    Flash\Entity, 0.5, 0.5
	xEntityFX       Flash\Entity, FX_FULLBRIGHT
	xRotateSprite   Flash\Entity, Rand(0, 180)
End Function

;Обновление вспышек
Function UpdateFlashes()
	For Flash.tFlash = Each tFlash
		If Flash\BurnTime < GameMillis
			xFreeEntity Flash\Entity
			xFreeEntity Flash\Point
			Delete Flash
		End If
	Next
End Function
