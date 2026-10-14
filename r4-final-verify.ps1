Write-Output '---HEAD---'
git log --oneline -1
Write-Output '---STATUS---'
git status --short
Write-Output '---DIFFSTAT---'
git --no-pager diff --stat
Write-Output '---COUNT---'
$t=0;$f=0;$s=0
Get-ChildItem 'app\build\test-results\testDebugUnitTest' -Filter '*.xml' | ForEach-Object {
  [xml]$x = Get-Content $_.FullName
  $t += [int]$x.testsuite.tests
  $f += [int]$x.testsuite.failures
  $s += [int]$x.testsuite.skipped
}
Write-Output "TESTS=$t FAILURES=$f SKIPPED=$s"