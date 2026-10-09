HTTP 메소드별 API 구현 과제. Java 21

[실행]

./gradlew bootRun

기본 포트는 8080

[구성]

v1  URL 경로와 요청 본문
v2  쿼리 파라미터
v3  요청 헤더

v1만 이용해도 과제의 요건은 달성 가능하나, v2와 v3의 방식 또한 이용해보고자 api를 넣어보았다.

[API (POST / GET / PUT / DELETE  총 9개)]

v1  POST    /api/v1/items                         단일 생성
v1  GET     /api/v1/items/{id}                    단건 조회
v1  PUT     /api/v1/items/{id}                    이름·가격 수정
v1  DELETE  /api/v1/items/{id}                    단건 삭제
v1  GET     /api/v1/items                         전체조회
v2  GET     /api/v2/items?keyword=&page=&size=    검색
v2  POST    /api/v2/items/bulk                    일괄 생성
v3  PUT     /api/v3/items/{id}/price              가격 수정
v3  DELETE  /api/v3/items                         전체 삭제

[사용한 응답 코드]

200  조회·수정·삭제 성공
201  생성 성공
400  입력 검증 실패, JSON 형식 오류, 잘못된 파라미터
401  Authorization 헤더 없이 전체 삭제 할려고 할때
404  없는 id, 없는 주소
409  같은 이름의 아이템이 이미 있음
500  ?simulate=500  (500번대 에러를 보기 위해 만듦. GET /api/v2/items?simulate=500 서버오류 확인용 테스트.)
503  ?simulate=503

[응답 형식]

모든 응답이 같은 모양이다.

성공  {"status":"success","data":{ ... }}

실패  {"status":"error","code":404,"message":"id=999 아이템을 찾을 수 없습니다."}

[미들웨어]

LoggingInterceptor 가 요청마다 시작과 완료를 콘솔에 출력한다(메소드, URL, 상태코드, 처리 시간 ms).

[구조]

Wsd2025Application.java       서버 시작점
LoggingInterceptor, WebConfig 미들웨어(요청 시작과 요청 완료를 출력)

Itemcontroller
요청을 받는 창구. URL과 메소드를 보고 어떤 일을 할지 정하고, 호출한 뒤 결과를 응답으로 포장. 버전별로 값을 받는 방식이 각기 다름. v1은 본문, v2는 쿼리 파라미터, v3는 헤더.

request                       클라이언트가 보낸 요청을 담는곳. 값이 올바른지 컨트롤러 실행 전에 검사.
ItemDto                       아이템을 나타내는 객체.(id, name, price)
ApiResponse                   모든 응답을 같은 모양으로 만드는 표준 응답 포멧
ApiException                  상태코드를 함께 담은 예외. 던지면 그 상태코드가 응답이 됨
GlobalExceptionHandler        컨트롤러에서의 예외를 에러 응답으로 바꿈.
ItemService                   메모리 저장소. (DB대신 map 사용. 서버 재시작 하면 초기화 됨)
