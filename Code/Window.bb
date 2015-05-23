;==================================================================
;Project Title:    BloodCore 
;Author:     	     (c) 2015 ObelardO
;Email:            obelardos@gmail.com
;Version:          Alpha #7
;Date:             18.05.15
;Notes:            Модуль окна
;==================================================================

;Параметры окна
Global WindowWidth  = 1024
Global WindowHeight = 768
Global WindowMode   = False 
Global WindowVsync  = True
Global WindowDepth  = 32
Global WindowAntial = 0

;Инициализация окна
Function InitWindow()
	SeedRnd xMillisecs()
	xSetTextureFiltering TF_ANISOTROPICX16
	xSetAntiAliasType WindowAntial  
	xGraphics3D WindowWidth, WindowHeight, WindowDepth, WindowMode, WindowVsync
	xAntiAlias True
	xAppTitle "BloodCore"
	xHidePointer

	WindowWidth  = xGraphicsWidth()
	WindowHeight = xGraphicsHeight()
End Function
