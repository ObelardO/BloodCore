;==================================================================
;Project Title:    BloodCore 
;Author:     	   (c) 2015 ObelardO
;Email:            obelardos@gmail.com
;Version:          Alpha #7
;Date:             18.05.15
;Notes:            Инструменты
;==================================================================

;Для плавного инкрементирования переменных
Function CurveValue#(newvalue#, oldvalue#, increments)
	If increments >  1 Then oldvalue# = oldvalue# - (oldvalue# - newvalue#) / increments 
	If increments <= 1 Then oldvalue# = newvalue# 
	Return oldvalue# 
End Function

;Простой парсер строк
Function StrCut$(InputString$, WordNum, Seperators$ = ";")

        FoundWord  = False
        WordsFound = 0

        For CharLoop = 1 To Len(InputString$)
        	ThisChar$ = Mid$(InputString$, CharLoop, 1)
            If Instr(Seperators$, ThisChar$, 1)
            	If FoundWord
                  	WordsFound = WordsFound + 1
                        If WordsFound = WordNum
                        	Return Word$
                        Else
					Word$ = ""
					FoundWord = False
                        End If
                  End If                                                
            Else
            	FoundWord = True
                Word$ = Word$ + ThisChar$                     
            End If
        Next    

        If (WordsFound + 1) = WordNum Return Word$ Else Return ""
End Function
