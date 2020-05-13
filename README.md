# Notepad
This repository is for Line Plus Challenge

<br><br>

<h2> 사용한 라이브러리 </h2>
<b>Glide</b>:   https://github.com/bumptech/glide <br>
<b>CircleImageView</b>:   https://github.com/hdodenhof/CircleImageView <br>
<b>AutoPermissions</b>:   https://github.com/pedroSG94/AutoPermissions/tree/master/app/src/main/java/com/pedro/autopermissions <br><br><br>


<h2> 앱 메커니즘 </h2>
<p> 기본적으로 SharedPreferences를 이용함</p>
<p> 각 메모를 구분하는 Key는 저장(수정)하는 시간이며, 모든 메모의 Key들을 names.xml 안에 저장한다.</p>
<p> 각 메모의 내용(제목, 내용, 사진)은 <수정시간>.xml에 저장한다. </p>
<p> 메모가 수정될 때에는 수정된 시간으로 Key를 수정하여 names.xml를 업데이트 하고, <기존수정시간>.xml을 지우고, <새수정시간>.xml 파일을 만든다.</p>
<p> 앨범에서 가져오는 사진과 카메라로 찍은 사진은 압축을 한 번 거친 뒤, 바이트로 만들고, 다시 String으로 수정하여 SharedPreferences에 저장한다.</p>
<p> URL로 첨부한 이미지는 링크만 String으로 저장한다.</p>
<p> 사용자가 첨부한 이미지는 첨부한 순서대로 태그를 매겨 관리한다.</p>
<p> 이미지의 순서 태그는 로컬사진의 경우 pic<순서>_ 를 URL 사진의 경우 URL<순서>_ 를 String의 맨 앞에 단다.</p>
<p> 유저가 글쓰기나 수정 도중 사진 첨부를 취소하는 경우 해당 사진 뒤에 첨부한 사진들의 태그를 하나 씩 앞으로 당긴다.</p>
<p> 리스트에 가장 먼저 첨부한 사진을 불러오거나, 메모상세보기에서 사진을 순서대로 불러올 때는 사진의 태그를 비교정렬 하여 순서대로 불러온다.</p>


<h2> 앱 사용법 </h2>
<p> 홈 화면은 메모리스트로 구성되며, 우측상단의 새메모 버튼을 눌러 새로운 메모를 쓰거나, 리스트에서 노트를 선택해 상세하게 볼 수 있다.</p>
<p> 새 글을 쓰거나 수정할 때는 이미지를 길게 눌러 첨부취소 할 수 있다.</p>
<p> 메모상세보기에서는 이미지를 클릭해 큰 화면으로 자세히 볼 수 있다.</p>

<h2> Result </h2>
<img src="https://github.com/rotmdwo/Notepad/blob/master/screenshot/result1.jpg?raw=true">
<img src="https://github.com/rotmdwo/Notepad/blob/master/screenshot/result2.jpg?raw=true">
<br>
<p> 결론적으로 전체 참가자 중 상위 48%의 성적을 받았다. </p>
<br>
<h3> 고쳐야 할 단점과 스스로 생각해본 해결방법</h3>
<p> ● 기능별 함수 구분이 안 되어 있음 / 클래스 내 메소드 분리가 되어 있지 않음.</p>
<p> ☞ OnCreate 메소드가 길어지면 안 된다. OnCreate 메소드는 간단명료하게 하고, 구현 부분은 Utility Class에서 구현하자.</p>
<br>
<p> ● 코딩컨벤션이 통일되어 있지 않음.</p>
<p> ☞ 앞으로는 프로젝트에 착수하기 전에 코딩컨벤션을 꼭 확인하고 하자. 예를 들어, 로컬변수/멤버변수 이름은 카멜표기법, 파이널 상수는 대문자 표기, for/while/if/case문 등 뒤에는 한 칸의 공백을 주고 괄호를 시작하는 등 이점을 꼭 상기하자.</p>
<br>
<p> ● context 변수를 static으로 사용하여 메모리 leak을 유발할 수 있음. </p>
<p> ☞ context 변수를 static으로 사용한다면 작은 앱에서는 문제가 없지만 액티비티를 많이 사용하는 앱에서는 메모리 leak을 유발하여 튕김의 원인이 된다. 앞으로는 다른 액티비티의 context가 필요한 액티비티의 생성자에서 context 변수를 받아오는 형태로 다른 액티비티의 메소드 및 멤버변수를 사용하자.</p>
<br>

<p> 위에 지적된 3가지를 개선하여 다음 과제에서는 상위 30% 안에 드는 것을 목표로 한다. </p>
<br>
<h3> 수정사항 </h3>
<p> ● 기능별 함수 구분이 안 되어 있음 / 클래스 내 메소드 분리가 되어 있지 않음.</p>
<p> ☞ Oncreate에 있는 기능들을 다른 메소드로 분리. 특히 사진을 로딩하는 작업들을 새로운 메소드로 분리하였다.</p>
<br>
<p> ● 코딩컨벤션이 통일되어 있지 않음.</p>
<p> ☞ Java 코딩컨벤션을 참조하여 전체적으로 수정하였다.</p>
<br>
<p> ● context 변수를 static으로 사용하여 메모리 leak을 유발할 수 있음. </p>
<p> ☞ context를 static으로 사용한 이유는 현재 액티비티에서 다른 액티비티의 변경사항을 업데이트 하기 위함이었다. 예를 들어, 메모를 새로 썼을 때 메모 리스트 UI를 업데이트 하고 싶었다. 하지만 Activity를 시작하면서 context를 넘겨주는 방법은 없었고, 다른 액티비티의 작업을 애초에 하면 안 된다는 결론을 얻었다. 리스트의 UI 업데이트는 리스트 액티비의 OnResume에서 업데이트 메소드를 실행하는 방안으로 변경하였다. </p>
<br>
