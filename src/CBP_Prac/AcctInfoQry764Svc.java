package CBP_Prac;

public class AcctInfoQry764Svc {
	// 로그 출력
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private ArrMngr arrMngr;
	private ArrBalMngr arrBalMngr;
	private CustMngr custMngr;
	private Cd cd;

	public AcctInfoQry764SvcGetDpstBasicInfoOut getDpstBasicInfo(AcctInfoQry764SvcGetDpstBasicInfoIn in)
			throws BizApplicationException {

		/**
		 * step1: 계약 객체 조회
		 */
		ArrReal arrReal = _getArrMngr().getArrRealByAcctNbr(in.getAcctNbr(), null);

		/**
		 * step2: 계약 정보에 의해서 출력 조립
		 */
		return _assembleArrBasicInfoOutput(arrReal);
	}
  
	
	/**
	 * 출력 정보 조립 
	 */

	private AccInfoQry764SvcGetDpstBasicInfoOut _assembleArrBasicInfoOutput(ArrReal arrReal)
			throws BizApplicationException {

		// 계약 기본 정보 조립
		AcctInfoQry764SvcGetDpstBasicInfoOut out = new AcctInfoQry764SvcGetDpstBasicInfoOut();

		List<ArrCnd> cndlist = arrReal.getListArrCndAll();
		out.setListCndDTO(ArrReturn(cndList));
	}

	private List<ArrCndOut> ArrReturn(List<ArrCnd> cndList) {

		List<ArrCndOut> arrCndList = new ArryList<ArrCndOut>();

		ArrCndOut arrCndOutDTO;

		for (ArrCnd arrCndIn : cndList) {

			arrCndOutDTO = new ArrCndOut();

			arrCndOutDTO.setArrCndCd(arrCndIn.getCndCd());
			arrCndOutDTO.setArrCndVal(arrCndIn.getCndVal());

			arrCndList.add(arrCndOutDTO);
		}

		return arrCndList;

	}

}
