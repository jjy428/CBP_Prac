import java.util.ArrayList;
import java.util.List;


/**
 * <b>BXM Service class</b>
 * <p>
 * <b>Revision history</b><br>
 * 
 * <pre>
 * 2022.02.15 : New creation
 * </pre>
 * 
 * @since 2022.02.15
 * @version 1.0.0
 * @author NB-21122716
 */
@BxmService("DepositGroupAccount764Svc")
@BxmCategory(logicalName = "모임통장7번4번", author = "NB-21122716")
public class DepositGroupAccount764Svc {
   private final Logger logger = LoggerFactory.getLogger(this.getClass());
 
   private ArrMngr arrMngr;
   private ArrSrvcCntr arrSrvcCntr;
   private ArrTxMngr arrTxMngr;
   private CustMngr custMngr;
   private ArrIntRtProvider arrIntRtProvider;


   @BxmServiceOperation("openGroupAccount74")
   @TransactionalOperation
   @CbbSrvcInfo(srvcCd = "SDP7649950", srvcNm = "Open Group Account74", srvcAbrvtnNm = "openGroupAccount74")
   public DepositGroupAccountOpen764SvcOpnGrpAcctOut openGroupAccount74(DepositGroupAccountOpen764SvcOpnGrpAcctIn in)
         throws BizApplicationException {

      _executeWithdrawalService(in);

      /**
       * Create arrangement
       */
      Arr arr = _createArr(in);

      /**
       * Service validation
       */
      _validateService(in, arr);

      /**
       * Get transaction last sequence number
       */
      int txSeqNbr = _getArrTxMngr().getTxSeqNbr(arr.getArrId());

      /**
       * Perform the conditions' action which are related service
       */
      _doServiceAction(in, arr, txSeqNbr);

      /**
       * Create arrangement transaction, entry and balance
       */
      ArrTx arrTx = _createTransactionHistory(in, arr, txSeqNbr);

      /**
       * Generate arrangement normal interest rate
       */
      _getArrIntRtProvider().generateArrDepositInterestRate(arr, arrTx.getTxDt(), arrTx.getTxSeqNbr());

      /**
       * Assemble and return service output
       */
      return _makeServiceOutput(arr);

   }

   private void _executeWithdrawalService(DepositGroupAccountOpen764SvcOpnGrpAcctIn in) throws BizApplicationException {
      
      if (StringUtils.isEmpty(in.getWhdrwlAcctNbr())) {
         return;
      }

      DpstWhdrwlSvcIn dpstWhdrwlSvcIn = new DpstWhdrwlSvcIn();

      dpstWhdrwlSvcIn.setAcctNbr(in.getWhdrwlAcctNbr());
      dpstWhdrwlSvcIn.setPswd(in.getWhdrwlAcctPswd());
      dpstWhdrwlSvcIn.setCrncyCd(in.getCrncyCd());
      dpstWhdrwlSvcIn.setTxAmt(in.getTxAmt());
      dpstWhdrwlSvcIn.setTrnsfrAmt(in.getTxAmt());
      dpstWhdrwlSvcIn.setRckngDt(in.getRckngDt());
      dpstWhdrwlSvcIn.setCustTxDscd(CustTxDscdDpstEnum.OPEN.getValue());

      CbbInternalServiceExecutor.execute("SDP0120200", dpstWhdrwlSvcIn);
      
   }

   private DepositGroupAccountOpen764SvcOpnGrpAcctOut _makeServiceOutput(Arr arr) throws BizApplicationException {

      DepositGroupAccountOpen764SvcOpnGrpAcctOut out = new DepositGroupAccountOpen764SvcOpnGrpAcctOut();

      out.setArrId(arr.getArrId());
      out.setCustId(arr.getMainArrCustId());
      out.setCustNm(_getCustMngr().getCust(arr.getMainArrCustId()).getName());
      out.setAcctNbr(arr.getAcctNbr());
      out.setOpnDt(arr.getArrOpnDt());
      out.setMtrtyDt(arr.getArrMtrtyDt());
      out.setPdNm(arr.getPd().getPdNm());

      return out;

   }

   private ArrIntRtProvider _getArrIntRtProvider() {
      arrIntRtProvider = CbbApplicationContext.getBean(ArrIntRtProvider.class, arrIntRtProvider);
      return arrIntRtProvider;
   }

   private ArrTx _createTransactionHistory(DepositGroupAccountOpen764SvcOpnGrpAcctIn in, Arr arr, int txSeqNbr)
         throws BizApplicationException {
      ArrTxStdFrmtIn arrTxStdFrmtIn = new ArrTxStdFrmtIn();

      arrTxStdFrmtIn.setTxCustId(_getCmnContext().getCustId());
      arrTxStdFrmtIn.setCrncyCd(in.getCrncyCd());
      arrTxStdFrmtIn.setTxSeqNbr(txSeqNbr);
      arrTxStdFrmtIn.setTxAmt(in.getTxAmt());
      arrTxStdFrmtIn.setRckngDt(in.getRckngDt());
      arrTxStdFrmtIn.setTxEntry(_setEntryInfo(arrTxStdFrmtIn, in));
      arrTxStdFrmtIn.setXtnInfo(_setTxXtnCntntIn(in, arr));

      return _getArrTxMngr().createArrTx(arrTxStdFrmtIn, arr);



   }

   private List<ArrTxXtnCntntIn> _setTxXtnCntntIn(DepositGroupAccountOpen764SvcOpnGrpAcctIn in, Arr arr) throws BizApplicationException {
   
      List<ArrTxXtnCntntIn> xtnInfo = new ArrayList<ArrTxXtnCntntIn>();

      ArrTxXtnCntntIn txXtnCntntIn = new ArrTxXtnCntntIn();

      txXtnCntntIn.setXtnAtrbtNm(ArrTxXtnInfoEnum.CUST_TX_DSCD.getValue());
      txXtnCntntIn.setXtnAtrbtCntnt(CustTxDscdDpstEnum.OPEN.getValue());
      xtnInfo.add(txXtnCntntIn);

      return xtnInfo;
   }

   private List<EntryIn> _setEntryInfo(ArrTxStdFrmtIn arrTxStdFrmtIn, DepositGroupAccountOpen764SvcOpnGrpAcctIn in)
         throws BizApplicationException {
      // TODO Auto-generated method stub
      List<EntryIn> entryInList = new ArrayList<EntryIn>();

      EntryIn entryIn = new EntryIn();

      entryIn.setAmtTpCd(AmtTpEnum.PRNCPL.getValue());
      entryIn.setCrncyCd(in.getCrncyCd());
      entryIn.setDpstWhdrwlDscd(DpstWhdrwlEnum.DPST.getValue());

      if (StringUtils.isEmpty(in.getWhdrwlAcctNbr())) {
         entryIn.setCashTrnsfrDscd(CashTrnsfrEnum.CASH.getValue());
         entryIn.setCashAmt(in.getTxAmt());
      } else {
         entryIn.setCashTrnsfrDscd(CashTrnsfrEnum.TRNSFR.getValue());
         entryIn.setTrnsfrAmt(in.getTxAmt());
      }

      entryIn.setTxAmt(in.getTxAmt());
      entryInList.add(entryIn);

      return entryInList;

   }

   private void _doServiceAction(DepositGroupAccountOpen764SvcOpnGrpAcctIn in, Arr arr, int txSeqNbr)
         throws BizApplicationException {
      // TODO Auto-generated method stub

      ArrActionRequiredValue arrActionRequiredValue = new ArrActionRequiredValue();

      arrActionRequiredValue.setTxDt(_getCmnContext().getTxDate());
      arrActionRequiredValue.setTxSeqNbr(txSeqNbr);
      arrActionRequiredValue.setRckngDt(in.getRckngDt());

      arr.doServiceAction(arrActionRequiredValue);

   }

   private void _validateService(DepositGroupAccountOpen764SvcOpnGrpAcctIn in, Arr arr) throws BizApplicationException {
      // TODO Auto-generated method stub

      _getArrSrvcCntr().validate(arr, null);

   }

   private Arr _createArr(DepositGroupAccountOpen764SvcOpnGrpAcctIn in) throws BizApplicationException {
      // TODO Auto-generated method stub
      ArrCrtnIn arrCrtnIn = new ArrCrtnIn();

      arrCrtnIn.setMndtryNegCndAutoBuildYn(CCM01.YES);

      arrCrtnIn.setArrBsicCrtn(_setArrBsicCrtn(in));
      arrCrtnIn.setArrRelList(_setArrRelList(in));
      arrCrtnIn.setArrXtnList(_setArrXtnAtrbtList(in));
      arrCrtnIn.setArrCndList(_setArrCndList(in));

      return _getArrMngr().openArr(arrCrtnIn);
   }

   private List<ArrCndCrtn> _setArrCndList(DepositGroupAccountOpen764SvcOpnGrpAcctIn in) throws BizApplicationException {
      // TODO Auto-generated method stub
      List<ArrCndCrtn> arrCndCrtnList = new ArrayList<ArrCndCrtn>();

      ArrCndCrtn arrCndCrtn = new ArrCndCrtn();

      arrCndCrtn.setCndCd(PdCndEnum.CURRENCY.getValue());
      arrCndCrtn.setTxtCndVal(in.getCrncyCd());
      arrCndCrtnList.add(arrCndCrtn);

      return arrCndCrtnList;

   }

   private List<ArrXtnCrtn> _setArrXtnAtrbtList(DepositGroupAccountOpen764SvcOpnGrpAcctIn in) throws BizApplicationException {
      // TODO Auto-generated method stub
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

      arrXtnCrtn.setXtnAtrbtNm("grpPsbkNm");
      arrXtnCrtn.setXtnAtrbtCntnt(in.getGrpPsbkNm());
      arrXtnCrtnList.add(arrXtnCrtn);

      return arrXtnCrtnList;

   }

   private List<ArrRelCrtn> _setArrRelList(DepositGroupAccountOpen764SvcOpnGrpAcctIn in) throws BizApplicationException {
      // TODO Auto-generated method stub
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
      arrRelCrtn.setRltdBizObjId(_getCmnContext().getDeptId());
      arrRelCrtnList.add(arrRelCrtn);

      return arrRelCrtnList;

   }

   private ArrBsicCrtn _setArrBsicCrtn(DepositGroupAccountOpen764SvcOpnGrpAcctIn in) throws BizApplicationException {
      // TODO Auto-generated method stub

      ArrBsicCrtn arrBsicCrtn = new ArrBsicCrtn();

      arrBsicCrtn.setAcctNbr(in.getWhdrwlAcctNbr());
      arrBsicCrtn.setPdCd(in.getPdCd());

      // If the the initial date in reckoning is blank, use system date
      if (!StringUtils.isEmpty(in.getRckngDt())) {
         arrBsicCrtn.setCrtnEfctvDt(in.getRckngDt());
      } else {
         arrBsicCrtn.setCrtnEfctvDt(_getCmnContext().getTxDate());
      }
      arrBsicCrtn.setArrStsChngRsnCd(ArrStsChngRsnEnum.ACTIVE_OPEN.getValue());

      return arrBsicCrtn;

   }

   private CmnContext _getCmnContext() {
      cmnContext = CbbApplicationContext.getBean(CmnContext.class, cmnContext);
      return cmnContext;
   }

   private ArrMngr _getArrMngr() {
      arrMngr = CbbApplicationContext.getBean(ArrMngr.class, arrMngr);
      return arrMngr;
   }

   private ArrSrvcCntr _getArrSrvcCntr() {
      arrSrvcCntr = CbbApplicationContext.getBean(ArrSrvcCntr.class, arrSrvcCntr);
      return arrSrvcCntr;
   }

   private ArrTxMngr _getArrTxMngr() {
      arrTxMngr = CbbApplicationContext.getBean(ArrTxMngr.class, arrTxMngr);
      return arrTxMngr;

   }

   private CustMngr _getCustMngr() {
      custMngr = CbbApplicationContext.getBean(CustMngr.class, custMngr);
      return custMngr;
   }
   
   private ArrIntRtProvider _getarrIntRtProvider() {
      arrIntRtProvider = (ArrIntRtProvider) CbbApplicationContext.getBean(ArrIntRtProvider.class, arrIntRtProvider);
      return arrIntRtProvider;
   }
}