package CBP_Prac;

public class CBP_Prac7 {
		   private final Logger logger = LoggerFactory.getLogger(this.getClass());

		   private ArrMngr arrMngr;
		   private CmnContext cmnContext;
		   private ArrSrvcCntr arrSrvcCntr;
		   private ArrTxMngr arrTxMngr;
		   private ArrIntRtProvider arrIntRtProvider;
		   private CustMngr custMngr;

		   @BxmServiceOperation("openGthrngArr")
		   @CbbSrvcInfo(srvcCd = "SED7649997", srvcNm = "Open Gathering Arrangement")
		   public DpstGthrngAcctOpn764SvcOpenGthrngAcctOut openGthrngArr(DpstGthrngAcctOpn764SvcOpenGthrngAcctIn in)
		         throws BizApplicationException {

		      /*
		       * step1: 출금연동이 있는 경우 출금에 대한 처리를 먼저 수행
		       */
		      _excuteWithdrawalService(in);

		      /*
		       * step2: 계약원장신규
		       */
		      Arr arr = _createArr(in);

		      /*
		       * step3: 서비스처리규칙검증
		       */
		      _validateService(in, arr);
  
		      /*
		       * step4: 거래일련번호 생성
		       */
		      int txSeqNbr = _getArrTxMngr().getTxSeqNbr(arr.getArrId());

		      /*
		       * step5: 조건규칙수행
		       */
		      _doServiceAction(in, arr, txSeqNbr);

		      /*
		       * step6: 거래생성
		       */
		      ArrTx tx = _createTransactionHistory(in, arr, txSeqNbr);

		      /*
		       * step7: 금리계산
		       */
		      _generateArrDepositInterestRate(arr, tx);

		      /*
		       * step8: 출력조립
		       */
		      return _makeServiceOutput(arr);

		   }

		   private void _excuteWithdrawalService(DpstGthrngAcctOpn764SvcOpenGthrngAcctIn in) throws BizApplicationException {
		      /*
		       * step1: 출금계좌번호가 입력되었을 경우 계좌신규금애만큼 해당 출금계좌로부터 출금하도록 수행
		       */

		      // 출금계좌번호 미입력된 경우 리턴
		      if (StringUtils.isEmpty(in.getWhdrwlAcctNbr())) {

		         return;

		      }

		      DpstWhdrwlSvcIn dpstWhdrwlIn = new DpstWhdrwlSvcIn();

		      // Generated by code generator [[
		      dpstWhdrwlIn.setAcctNbr(in.getWhdrwlAcctNbr());// set [계좌번호]
		      dpstWhdrwlIn.setPswd(in.getWhdrwlAcctPswd());// set [비밀번호]
		      dpstWhdrwlIn.setCrncyCd(in.getCrncyCd());// set [통화코드]
		      dpstWhdrwlIn.setTxAmt(in.getTxAmt());// set [거래금액]
		      dpstWhdrwlIn.setTrnsfrAmt(in.getTxAmt());// set [대체금액]
		      dpstWhdrwlIn.setRckngDt(in.getRckngDt());// set [기산년월일]
		      dpstWhdrwlIn.setCustTxDscd(CustTxDscdDpstEnum.OPEN.getValue());// set [고객거래구분코드]
		      // Generated by code generator ]]
		      CbbInternalServiceExecutor.execute("SDP0120200", dpstWhdrwlIn);

		   }

		   private Arr _createArr(DpstGthrngAcctOpn764SvcOpenGthrngAcctIn in) throws BizApplicationException {
		      /*
		       * step2: 모임통장계약 신규
		       */
		      ArrCrtnIn arrCrtnIn = new ArrCrtnIn();
		      arrCrtnIn.setMndtryNegCndAutoBuildYn(CCM01.YES);

		      /*
		       * step2-1: 기본정보 조립
		       */
		      arrCrtnIn.setArrBsicCrtn(_setArrBsicCrtn(in));

		      /*
		       * step2-2: 관계정보 조립
		       */
		      arrCrtnIn.setArrRelList(_setArrRelList(in));

		      /*
		       * step2-3: 확장속성정보 조립
		       */
		      arrCrtnIn.setArrXtnList(_setArrXtnAtrbtList(in));

		      /*
		       * step2-4: 조건정보 조립
		       */
		      arrCrtnIn.setArrCndList(_setArrCndList(in));

		      return _getArrMngr().openArr(arrCrtnIn);

		   }

		   private ArrBsicCrtn _setArrBsicCrtn(DpstGthrngAcctOpn764SvcOpenGthrngAcctIn in) throws BizApplicationException {
		      /*
		       * step2-1: 기본정보 조립
		       */

		      ArrBsicCrtn arrBsicCrtn = new ArrBsicCrtn();

		      // 신규하고자 하는 상품코드
		      arrBsicCrtn.setPdCd(in.getPdCd());
		      arrBsicCrtn.setPswd(in.getPswd());

		      // 계약원장생성 기준일자: 기산일자를 입력받은 경우 기산일자 입력
		      if (!StringUtils.isEmpty(in.getRckngDt())) {
		         arrBsicCrtn.setCrtnEfctvDt(in.getRckngDt());
		      } else {
		         arrBsicCrtn.setCrtnEfctvDt(_getcmnContext().getTxDate());
		      }

		      // 계약상태변경사유코드: 계좌신규로 인한 활동상태
		      arrBsicCrtn.setArrStsChngRsnCd(ArrStsChngRsnEnum.ACTIVE_OPEN.getValue());

		      return arrBsicCrtn;

		   }

		   private List<ArrRelCrtn> _setArrRelList(DpstGthrngAcctOpn764SvcOpenGthrngAcctIn in) throws BizApplicationException {
		      /*
		       * step2-2: 관계정보 조립
		       */
		      List<ArrRelCrtn> arrRelCrtnList = new ArrayList<ArrRelCrtn>();

		      // 계약고객관계 : 관계유형코드 - 주계약자
		      ArrRelCrtn arrRelCrtn = new ArrRelCrtn();

		      arrRelCrtn.setArrRelKndCd(ArrRelKndEnum.CUSTOMER.getValue());
		      arrRelCrtn.setArrRelCd(ArrCustRelEnum.MAIN_CONTRACTOR.getValue());
		      arrRelCrtn.setRltdBizObjId(in.getCustId());
		      arrRelCrtnList.add(arrRelCrtn);

		      arrRelCrtn = new ArrRelCrtn();
		      arrRelCrtn.setArrRelKndCd(ArrRelKndEnum.DEPARTMENT.getValue());
		      arrRelCrtn.setArrRelCd(ArrDeptRelEnum.MANAGEMENT_UNIT.getValue());
		      arrRelCrtn.setRltdBizObjId(_getcmnContext().getDeptId());
		      arrRelCrtnList.add(arrRelCrtn);

		      return arrRelCrtnList;

		   }

		   private List<ArrXtnCrtn> _setArrXtnAtrbtList(DpstGthrngAcctOpn764SvcOpenGthrngAcctIn in)
		         throws BizApplicationException {
		      /*
		       * step2-3: 확장속성정보 조립
		       */
		      List<ArrXtnCrtn> arrXtnCrtnList = new ArrayList<ArrXtnCrtn>();

		      ArrXtnCrtn arrXtnCrtn = new ArrXtnCrtn();

		      arrXtnCrtn.setXtnAtrbtNm(ArrXtnInfoEnum.PASSBOOK_ISSUE_YN.getValue());
		      arrXtnCrtn.setXtnAtrbtCntnt(CCM01.NO);
		      arrXtnCrtnList.add(arrXtnCrtn);

		      if (CCM01.NO.equals(in.getTermsAgrmntYn())) {
		         throw new BizApplicationException("AAPDPE0027", null);
		      }

		      arrXtnCrtn = new ArrXtnCrtn();

		      arrXtnCrtn.setXtnAtrbtNm(ArrXtnInfoEnum.TERMS_AGREEMENT_YN.getValue());
		      arrXtnCrtn.setXtnAtrbtCntnt(in.getTermsAgrmntYn());
		      arrXtnCrtnList.add(arrXtnCrtn);

		      arrXtnCrtn = new ArrXtnCrtn();

		      arrXtnCrtn.setXtnAtrbtNm("gthrngPsbkNm");
		      arrXtnCrtn.setXtnAtrbtCntnt(in.getGthrngPsbkNm());
		      arrXtnCrtnList.add(arrXtnCrtn);

		      return arrXtnCrtnList;

		   }

		   private List<ArrCndCrtn> _setArrCndList(DpstGthrngAcctOpn764SvcOpenGthrngAcctIn in) throws BizApplicationException {
		      /*
		       * step2-4: 조건정보 조립
		       */
		      List<ArrCndCrtn> arrCndCrtnList = new ArrayList<ArrCndCrtn>();

		      ArrCndCrtn arrCndCrtn = new ArrCndCrtn();

		      arrCndCrtn.setCndCd(PdCndEnum.CURRENCY.getValue());
		      arrCndCrtn.setTxtCndVal(in.getCrncyCd());
		      arrCndCrtnList.add(arrCndCrtn);

		      return arrCndCrtnList;

		   }

		   private void _validateService(DpstGthrngAcctOpn764SvcOpenGthrngAcctIn in, Arr arr) throws BizApplicationException {
		      /*
		       * step3: 서비스처리규칙 검증
		       */
		      _getArrSrvcCntr().validate(arr, null);

		   }

		   private void _doServiceAction(DpstGthrngAcctOpn764SvcOpenGthrngAcctIn in, Arr arr, int txSeqNbr)
		         throws BizApplicationException {
		      /*
		       * step4: 조건규칙수행
		       */
		      ArrActionRequiredValue arrActionRequiredValue = new ArrActionRequiredValue();

		      arrActionRequiredValue.setTxSeqNbr(txSeqNbr);
		      arrActionRequiredValue.setRckngDt(in.getRckngDt());
		      arrActionRequiredValue.setTxDt(_getcmnContext().getTxDate());
		      arr.doServiceAction(arrActionRequiredValue);

		   }

		   private ArrTx _createTransactionHistory(DpstGthrngAcctOpn764SvcOpenGthrngAcctIn in, Arr arr, int txSeqNbr)
		         throws BizApplicationException {
		      /*
		       * step6: 거래생성
		       */
		      /*
		       * 거래기본정보
		       */
		      ArrTxStdFrmtIn arrTxStdFrmtIn = new ArrTxStdFrmtIn();

		      arrTxStdFrmtIn.setTxCustId(_getcmnContext().getCustId());
		      arrTxStdFrmtIn.setCrncyCd(in.getCrncyCd());
		      arrTxStdFrmtIn.setTxSeqNbr(txSeqNbr);
		      arrTxStdFrmtIn.setTxAmt(in.getTxAmt());
		      arrTxStdFrmtIn.setRckngDt(in.getRckngDt());

		      /*
		       * 거래 입출금내역
		       */
		      arrTxStdFrmtIn.setTxEntry(_setEntryInfo(arrTxStdFrmtIn, in));

		      /*
		       * 거래확장정보
		       */
		      arrTxStdFrmtIn.setXtnInfo(_setTxXtnCntntIn(in, arr));

		      return _getArrTxMngr().createArrTx(arrTxStdFrmtIn, arr);

		   }

		   private List<EntryIn> _setEntryInfo(ArrTxStdFrmtIn arrTxStdFrmtIn, DpstGthrngAcctOpn764SvcOpenGthrngAcctIn in)
		         throws BizApplicationException {
		      /*
		       * step6-2: 거래입출내역 조립
		       */
		      List<EntryIn> entryInList = new ArrayList<EntryIn>();

		      EntryIn entryIn = new EntryIn();

		      entryIn.setAmtTpCd(AmtTpEnum.PRNCPL.getValue());
		      entryIn.setDpstWhdrwlDscd(DpstWhdrwlEnum.DPST.getValue());
		      entryIn.setCrncyCd(in.getCrncyCd());

		      // 현금대체구분 정보 설정
		      if (!StringUtils.isEmpty(in.getWhdrwlAcctNbr())) {
		         // 출금계좌번호가 입력된 경우 대체
		         entryIn.setCashTrnsfrDscd((CashTrnsfrEnum.TRNSFR.getValue())); // 현금대체구분코드 : 대체
		         entryIn.setTrnsfrAmt(in.getTxAmt()); // 대체금액 : 거래금액
		      } else {
		         entryIn.setCashTrnsfrDscd(CashTrnsfrEnum.CASH.getValue()); // 현금대체구분코드 : 현금
		         entryIn.setCashAmt(in.getTxAmt()); // 현금 : 거래금액
		      }

		      entryIn.setTxAmt(in.getTxAmt());
		      entryInList.add(entryIn);

		      return entryInList;

		   }

		   private List<ArrTxXtnCntntIn> _setTxXtnCntntIn(DpstGthrngAcctOpn764SvcOpenGthrngAcctIn in, Arr arr)
		         throws BizApplicationException {
		      /*
		       * step6-3: 거래확장정보 조립
		       */
		      List<ArrTxXtnCntntIn> xtnInfo = new ArrayList<ArrTxXtnCntntIn>();

		      ArrTxXtnCntntIn txXtnCntntIn = new ArrTxXtnCntntIn();

		      txXtnCntntIn.setXtnAtrbtNm(ArrTxXtnInfoEnum.CUST_TX_DSCD.getValue());
		      txXtnCntntIn.setXtnAtrbtCntnt(CustTxDscdDpstEnum.OPEN.getValue());
		      xtnInfo.add(txXtnCntntIn);

		      return xtnInfo;

		   }

		   private void _generateArrDepositInterestRate(Arr arr, ArrTx tx) throws BizApplicationException {
		      /*
		       * step7: 금리계산
		       */
		      if (tx != null) {
		         _getArrIntRtProvider().generateArrDepositInterestRate(arr, tx.getTxDt(), tx.getTxSeqNbr());
		      }
		   }

		   private DpstGthrngAcctOpn764SvcOpenGthrngAcctOut _makeServiceOutput(Arr arr) throws BizApplicationException {
		      /*
		       * step8: 출력조립
		       */
		      DpstGthrngAcctOpn764SvcOpenGthrngAcctOut out = new DpstGthrngAcctOpn764SvcOpenGthrngAcctOut();

		      // Generated by code generator [[
		      out.setArrId(arr.getArrId());// set [계약식별자]
		      out.setCustId(arr.getMainArrCustId());// set [고객식별자]
		      out.setCustNm(_getCustMngr().getCust(arr.getMainArrCustId()).getName());// set [고객명]
		      out.setAcctNbr(arr.getAcctNbr());// set [계좌번호]
		      out.setOpnDt(arr.getArrOpnDt());// set [신규년월일]
		      out.setMtrtyDt(arr.getArrMtrtyDt());// set [만기년월일]
		      out.setPdNm(arr.getPd().getPdNm());// set [상품명]
		      // Generated by code generator ]]

		      return out;

		   }

		   private ArrMngr _getArrMngr() throws BizApplicationException {
		      arrMngr = (ArrMngr) CbbApplicationContext.getBean(ArrMngr.class, arrMngr);

		      return arrMngr;

		   }

		   private ArrTxMngr _getArrTxMngr() throws BizApplicationException {
		      arrTxMngr = (ArrTxMngr) CbbApplicationContext.getBean(ArrTxMngr.class, arrTxMngr);

		      return arrTxMngr;

		   }

		   private ArrSrvcCntr _getArrSrvcCntr() throws BizApplicationException {
		      arrSrvcCntr = (ArrSrvcCntr) CbbApplicationContext.getBean(ArrSrvcCntr.class, arrSrvcCntr);

		      return arrSrvcCntr;

		   }

		   private CmnContext _getcmnContext() throws BizApplicationException {
		      cmnContext = (CmnContext) CbbApplicationContext.getBean(CmnContext.class, cmnContext);

		      return cmnContext;

		   }

		   private CustMngr _getCustMngr() throws BizApplicationException {
		      custMngr = (CustMngr) CbbApplicationContext.getBean(CustMngr.class, custMngr);

		      return custMngr;

		   }

		   private ArrIntRtProvider _getArrIntRtProvider() throws BizApplicationException {
		      arrIntRtProvider = (ArrIntRtProvider) CbbApplicationContext.getBean(ArrIntRtProvider.class, arrIntRtProvider);

		      return arrIntRtProvider;

		   }

		
}