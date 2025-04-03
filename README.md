# Yutnori 
![yutnori](https://github.com/user-attachments/assets/d300272d-0ffa-415e-b236-ac554dbbed09)

## develop period
2025.04.03 ~

## Commit Convention
타입(#이슈번호): 부연 설명 및 이유
> ex. feat(#1): Login 화면 UI 구현

### 접두사
|    타입    | 설명 |
|:--------:|--|
|   feat   | 기능, 사용자 경험에 변경이 있는 경우 (모든 커밋은 기본적으로 feat)|
|   fix    | 이슈 또는 버그 해결 |
| refactor | 기능, 사용자 경험에 변경 없이 코드 구조를 개선한 경우 (패키지 변경, 변수명 변경, 가독성 개선 등) |
|  style   | 들여쓰기, 공백 등 코드 스타일 또는 여백/글자 크기 등 UI 요소의 변경 |
|   docs   | 주석, README.md 등 문서의 변경 |
|  remove  | 코드나 파일 삭제 |

---
## Branch Convention
(feat/fix/refactor/chore)/#이슈번호

> ex) feat/#2

---
## Git Flow
Issue 생성
</br>
Branch 생성
</br>
add → commit → push → pull request 과정을 거친다.
</br>
코드 리뷰 진행 후 모든 팀원들의 승인을 받는다.
</br>
develop branch로 merge한다.
</br>
develop branch로 이동하여 pull을 받은 다음 위 과정을 반복한다.
