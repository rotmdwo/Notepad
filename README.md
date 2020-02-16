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
