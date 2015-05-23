;==================================================================
;Project Title:    BloodCore 
;Author:     	   (c) 2015 ObelardO
;Email:            obelardos@gmail.com
;Version:          Alpha #7
;Date:             18.05.15
;Notes:            Модуль музыки
;==================================================================

;Режим проигрывания музыки
Const MusicStatusBreak  = 0
Const MusicStatusBattle = 1

;Режим музыки
Global MusicStatus

;Параметры музыки
Global MusicMasterVolume# = 1.0
Global MusicBreak
Global MusicBreakChannel
Global MusicBreakChannelVolume#
Global MusicBattle
Global MusicBattleChannel
Global MusicBattleChannelVolume#

;Параметры для затихания
Global MusicTauntMode
Global MusicTauntTime = 2500
Global MusicTauntTimer = 0

;Инициализация модуля музыки
Function InitMusic()
	MusicBreak  = xLoadSound("base\music\break.ogg")
	xLoopSound MusicBreak
	MusicBreakChannel = xPlaySound(MusicBreak)
	xChannelVolume MusicBreakChannel, 0.0

	MusicBattle = xLoadSound("base\music\battle.ogg")
	xLoopSound MusicBattle
	MusicBattleChannel = xPlaySound(MusicBattle)
	xChannelVolume MusicBattleChannel, 0.0
End Function

;Обновление музыки
Function UpdateMusic()
	Select MusicStatus
		Case MusicStatusBreak
			If MusicBreakChannelVolume  < MusicMasterVolume MusicBreakChannelVolume  = MusicBreakChannelVolume  + 0.005
			If MusicBattleChannelVolume > 0.0 MusicBattleChannelVolume = MusicBattleChannelVolume - 0.005

			If MusicTauntMode MusicBreakChannelVolume = MusicMasterVolume * 0.5

		Case MusicStatusBattle
			If MusicBreakChannelVolume  > 0.0 MusicBreakChannelVolume  = MusicBreakChannelVolume  - 0.005
			If MusicBattleChannelVolume < MusicMasterVolume MusicBattleChannelVolume = MusicBattleChannelVolume + 0.005

			If MusicTauntMode MusicBattleChannelVolume = MusicMasterVolume * 0.5
	End Select

	If MusicTauntMode And GameMillis > MusicTauntTimer MusicTauntMode = False

	xChannelVolume MusicBreakChannel,  MusicBreakChannelVolume
	xChannelVolume MusicBattleChannel, MusicBattleChannelVolume
End Function

;Затихание музыки
Function MusicTaunt()
	MusicTauntMode = True
	MusicTauntTimer = GameMillis + MusicTauntTime
End Function
