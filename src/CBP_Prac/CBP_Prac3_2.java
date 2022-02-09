package CBP_Prac;

public class CBP_Prac3_2 {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private ArrBalMngr		arrBalMngr;
	private CustMngr		custMngr;
	private ArrCustMngt		arrCustMngr;
	
	@BxmServiceOperation("getListCustRltdArrInfo")
	@CbbSrvcInfo(srvcCd = "SED7649982", srvcNm = "Get Arrangement Information List by Cust", srvcAbrvtnNm = "getListCustRltdArrInfo")
	public ArrInfoQryByCust764SvcGetListArrInfoOut getListCustRltdArrInfo(ArrInfoQryByCust764SvcGetListArrInfoIn in) throws BisApplicationException {
		
		ArrInfoQryByCust764SvcGetListArrInfoOut out = new ArrInfoQryByCust764SvcGetListArrInfoOut();
		
		//고객 객체를 조회해서 고객명 출력
		
		Cust cust = _getCustMngr().getCust(in.getCustId());
		out.setCustNm(cust.getName());
		
		//고객이 소유한 활동상태인 모든 수신계약 조회
		
		List<Arr> arrList = _getArrCustMngr().GetListCustOwnDepositArrActive(cust.getCustId());
		
		List<ArrInfoQryByCust764SvcGetListArrInfo> outArrList = new ArrayList(ArrInfoQryByCust764SvcArrInfo>();
		
		ArrInfoQryByCust764SvcArrInfo outArr;
		
		for (Arr arr : arrList) {
			
			outArr = new ArrInfoQryByCust764SvcGetListArrInfo();
			
			outArr.setArrId(arr.getArrId());
			outArr.setPdNm(arr.getPd().getPdNm());
			ArrBal currentBal = _getArrBalMngr().getArrBal(arr, AmtTpEnum.PRNCPL.getValue(), BalTpEnum.CURRENT.getValue(), arr.getLastBal());
			outArr.setLastBal(currentBal.getLastBal());
			outArr.setAcctNbr(arr.getAcctNbr());
			ouotArrList.add(outArr);
		}
		out.setArrInfoList(outArrList);
		
		return out;
		
	}
	
	private CustMngr _getCustMngr() throws BizApplicationException {
		custMngr = (CustMngr) CbbApplicationContext.getBean(CustMngr.class, custMngr);
		
		return custMngr;
		
	}
	
	private ArrBalMngr _getarrBalMngr() throws BizApplicationException {
		arrBalMngr = (ArrBalMngr) CbbApplicationContext.getBean(ArrBalMngr.class, arrBalMngr);
		
		return arrBalMngr;
		
	}
	
	private ArrCustMngr _getArrCustMngr() throws BizApplicationException {
		arrCustMngr = (ArrCustMngr) CbbApplicationContext.getBean(ArrCustMngr.class, arrCustMngr);
		
		return arrCustMngr;
		
	}
}
