package CBP_Prac;

import java.util.ArrayList;
import java.util.List;

public class CBP_Prac4 {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	   private ArrMngr arrMngr;
	   private ArrBalMngr arrBalMngr;
	   private CmnContext cmnContext;
	   private ArrTxProvider arrTxProvider;
	   private Cd cd;
	   
	   @BxmServiceOperation("getListTxInfo")
	   @CbbSrvcInfo(srvcCd = "SED7649994", srvcNm = "get list of transaction information")
	   //TxInfo에 대한 getList 하는 프로그램
	   public TxQry764SvcGetListTxInfoOut getListTxInfo(TxQry764SvcGetListTxInfoIn in) throws BizApplicationException {
	      
	      //step1: 계약 객체 획득
	      ArrReal arrReal = _getArrMngr().getArrRealByAcctNbr(in.getAcctNbr(), null);        //ArrTx란 애들은 Arr이 무조건 있어야함
	      
	      //step2: 계좌 거래내역 및 잔액 정보 조회
	      return _getTransactionInfo(arrReal);      //거래내역 정보를 가져와라
	      
	   }

	   /**
	    * step2: 계좌거래내역 및 잔액정보조회
	    */

	   private TxQry764SvcGetListTxInfoOut _getTransactionInfo(ArrReal arrReal) throws BizApplicationException {
	      // 거래조회 조건 조립
	      ArrTxInqryDtIn arrTxInqryDtIn = new ArrTxInqryDtIn();
	      arrTxInqryDtIn.setInqryStartDt(arrReal.getArrOpnDt());
	      arrTxInqryDtIn.setInqryEndDt(_getCmnContext().getTxDate());

	      //계좌 거래내역 정보 조립
	      List<ArrTxBsicIO> arrTxInfoList = getTransactionInfo(arrReal, arrTxInqryDtIn);
	      // 목록형 OMM 조립
	      TxQry764SvcGetListTxInfoOut out = getTxInfoLoop(arrReal, arrTxInfoList);
	      //잔액 조립
	      getLastBal(arrReal, out);
	      
	      return out;
	      
	   }

	   private TxQry764SvcGetListTxInfoOut getTxInfoLoop(ArrReal arrReal, List<ArrTxBsicIO> arrTxInfoList) {
	      TxQry764SvcGetListTxInfoOut out = new TxQry764SvcGetListTxInfoOut();
	      List<TxQry764SvcTxInfo> outList = new ArrayList<>();

	      for (ArrTxBsicIO txInfo : arrTxInfoList) {

	         TxQry764SvcTxInfo txInfoOut = new TxQry764SvcTxInfo();

	         // Generated by code generator [[
	         txInfoOut.setTxDt(txInfo.getTxDt());// set [거래년월일]
	         txInfoOut.setTxSeqNbr(txInfo.getTxSeqNbr());// set [거래일련번호]
	         txInfoOut.setTxCd(txInfo.getTxCd());// set [거래코드]
	         txInfoOut.setTxStsCd(txInfo.getTxStsCd());// set [거래상태코드]
	         txInfoOut.setTxStsCdNm(_getCd().getCodeName(CdNbrEnum.TRANSACTION_STATUS_CD.getValue(), txInfo.getTxStsCd()));// set [거래상태코드명]
	         // Generated by code generator ]]
	         
	         outList.add(txInfoOut);
	      
	      }
	      
	      out.setTotCnt(arrTxInfoList.size());
	      out.setTxInfoList(outList);
	      out.setAcctNbr(arrReal.getAcctNbr());
	      return out;
	   }

	   private List<ArrTxBsicIO> getTransactionInfo(ArrReal arrReal, ArrTxInqryDtIn arrTxInqryDtIn) {
	      List<ArrTxBsicIO> arrTxInfoList = _getArrTxProvider().getListArrTxHistory(arrTxInqryDtIn, arrReal);
	      return arrTxInfoList;
	   }

	   private void getLastBal(ArrReal arrReal, TxQry764SvcGetListTxInfoOut out) {
	      //계좌 잔액객체 조회
	      ArrBal currentBal = _getArrBalMngr().getArrPrincipalBal(arrReal,  arrReal.getCrncyCd());
	      
	      //최종잔액
	      out.setCrrntBal(currentBal.getLastBal());
	      out.setBalDt(currentBal.getBalDt());
	   }

	   private ArrMngr _getArrMngr() throws BizApplicationException {
	      arrMngr = (ArrMngr) CbbApplicationContext.getBean(ArrMngr.class, arrMngr);

	      return arrMngr;

	   }

	   private CmnContext _getCmnContext() {
	      cmnContext = CbbApplicationContext.getBean(CmnContext.class, cmnContext);

	      return cmnContext;

	   }

	   private ArrTxProvider _getArrTxProvider() {
	      arrTxProvider = CbbApplicationContext.getBean(ArrTxProvider.class, arrTxProvider);

	      return arrTxProvider;

	   }

	   private ArrBalMngr _getArrBalMngr() throws BizApplicationException {
	      arrBalMngr = (ArrBalMngr) CbbApplicationContext.getBean(ArrBalMngr.class, arrBalMngr);

	      return arrBalMngr;

	   }

	   private Cd _getCd() throws BizApplicationException {
	      cd = (Cd) CbbApplicationContext.getBean(Cd.class, cd);

	      return cd;

	   }
	}
}
