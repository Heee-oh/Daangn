-- member_region 테이블에 외래키 추가
ALTER TABLE "member_region"
    ADD CONSTRAINT fk_member_region_member
        FOREIGN KEY ("member_id") REFERENCES "member"("member_id");

-- member_interest 테이블에 외래키 추가
ALTER TABLE "member_interest"
    ADD CONSTRAINT fk_member_interest_member
        FOREIGN KEY ("member_id") REFERENCES "member"("member_id");