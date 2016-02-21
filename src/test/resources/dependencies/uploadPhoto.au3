#include <MsgBoxConstants.au3>
WinWait("Выгрузка файла", "", 6)
Local $iWinExists = WinExists("Выгрузка файла")
If Not $iWinExists Then
	MsgBox($MB_SYSTEMMODAL, "", "Окно для ввода пути к файлу изображения не найдено.")
	Exit
Else
	ControlSetText("Выгрузка файла", "", "[CLASS:Edit]", "")
	ControlSetText("Выгрузка файла", "", "[CLASS:Edit]", $CmdLine[1])
	ControlClick("Выгрузка файла", "", "[CLASS:Button; INSTANCE:1]")
EndIf


