#include <MsgBoxConstants.au3>
WinWait("�������� �����", "", 6)
Local $iWinExists = WinExists("�������� �����")
If Not $iWinExists Then
	MsgBox($MB_SYSTEMMODAL, "", "���� ��� ����� ���� � ����� ����������� �� �������.")
	Exit
Else
	ControlSetText("�������� �����", "", "[CLASS:Edit]", "")
	ControlSetText("�������� �����", "", "[CLASS:Edit]", $CmdLine[1])
	ControlClick("�������� �����", "", "[CLASS:Button; INSTANCE:1]")
EndIf


