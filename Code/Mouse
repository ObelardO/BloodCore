;==================================================================
;Project Title:    BloodCore 
;Author:     	   (c) 2015 ObelardO
;Email:            obelardos@gmail.com
;Version:          Alpha #7
;Date:             18.05.15
;Notes:            Модуль мыши
;==================================================================

;Параметры и флаги для мыши
Global MouseSens# = 0.03
Global MouseSpeedX#
Global MouseSpeedY#
Global MouseSpeedZ
Global MousehitLeft
Global MouseDownLeft
Global MouseHitRight
Global MouseDownRight

;Обновление мыши
Function UpdateMouse()
	MouseSpeedX = xMouseXSpeed()
	MouseSpeedY = xMouseYSpeed()
	MouseSpeedZ = xMouseZSpeed()
	MousehitLeft = xMouseHit(MOUSE_LEFT)
	MouseDownLeft = xMouseDown(MOUSE_LEFT)
	MousehitRight = xMouseHit(MOUSE_RIGHT)
	MouseDownRight = xMouseDown(MOUSE_RIGHT)

	xMoveMouse WindowWidth * 0.5, WindowHeight * 0.5
End Function
