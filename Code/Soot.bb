Const SootLiveTime = 8000

Global SootTexture

Type tSoot
	Field Entity
	Field LiveTime
	Field Escaping
	Field Alpha#
End Type

Function InitSoots()
	SootTexture = xLoadTexture("base\textures\soot.png", FLAGS_ALPHA)
End Function

Function CreateSoot(SootX# = 0, SootZ# = 0)
	Local Size# = Rnd(12, 18)

	Soot.tSoot = New tSoot

	Soot\LiveTime = GameMillis + SootLiveTime

	Soot\Entity = CreateSootMesh()
	xEntityTexture  Soot\Entity, SootTexture
	xScaleEntity    Soot\Entity, Size, 1, Size
	xPositionEntity Soot\Entity, SootX, 0.05, SootZ
	xRotateEntity   Soot\Entity, 0, Rand(360), 0
End Function

Function UpdateSoots()
	For Soot.tSoot = Each tSoot
		If Soot\Escaping	
			Soot\Alpha = Soot\Alpha - 0.05
			xEntityAlpha Soot\Entity, Soot\Alpha

			If Soot\Alpha <= 0
				xFreeEntity Soot\Entity
				Delete Soot
			End If
		ElseIf Soot\LiveTime < GameMillis
			Soot\Escaping = True
			Soot\Alpha = 1.0
		End If
	Next
End Function 

Function CreateSootMesh()
	; Creation mesh:
	Mesh = xCreateMesh()
	Surface = xCreateSurface(mesh)

	; Vertex adding:
	v1 = xAddVertex(Surface, 0.5, 0, 0.5, 0, 1)
	v2 = xAddVertex(Surface, 0.5, 0,-0.5, 0, 0)
	v3 = xAddVertex(Surface,-0.5, 0,-0.5, 1, 0)
	v4 = xAddVertex(Surface,-0.5, 0, 0.5, 1, 1)

	; Triangle adding:
	tri1 = xAddTriangle(Surface, v1, v2, v4)
	tri2 = xAddTriangle(Surface, v3, v4, v2)

	; Return updated mesh:
	xUpdateNormals Mesh
	Return Mesh 
End Function

Function ResetSoot()
	For Soot.tSoot = Each tSoot
		xFreeEntity Soot\Entity
		Delete Soot
	Next
End Function
