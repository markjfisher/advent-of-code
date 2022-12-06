cls
? "AOC 2022 Day 06 - FastBasic"

data inp_dt() b. = "qfmfhmhjmjggwbbvdvwvlvrrtsrsccwsslvlffjrrtprprjjvmmclmmghhddpvddclctcqtccgbgdbgdgsdgghqhtqtvvptvppwr",
data          b. = "wprpvrrrhpphththvhhrnnhnlnslnlhnhnhgnhnpnqqsmsgsllprlprrlzzqzffmzztctbtnbtthlttqvqcqmcmpcpbbczzbqbgg",
data          b. = "hcghchhvwvllfrfnnbssfzsszpsplpglpprnpnfnbnhbnbtbzzvbvpbbhjjlzzbtbvbppczppbwppqwwnwlwccglgvgrgmmdwmwr",
data          b. = "mrppnfnhhhhqthqthqqrhrshhhqbhqqjgjvjllzvzbzhbbpttjsszvzqqtzzmbmddpldpdcdnccrmcrmmpwprplrrqssvddmpdmm",
data          b. = "wfwwlrljrrdsssmhsspnpffjggqllnzlnlhnnmddfrfpfbbvssjsrrznngcghgchcmhmrrrtzztjzzhchssslsmlmvvpwpqpjqjd",
data          b. = "ddmsdmmtgtmgtglttfbbgrrcprrqffmmjnjttcmczzgbzggthhsttpggrmmgwwnpnqnqvnqvqppmlpmlpmpjpljljmmtpptfppfr",
data          b. = "ppfdpfdppddmdttgzgzzdbzdzhzhnnsqssvmvbbpjjzwwvnwvvzmvzmzpmpttvrvccqddpgdppgmmthmthtggsfsbbvfbvfvhfvh",
data          b. = "vvpwpddqrqgqhghnnfmfbbwrbrgbgbvgbbdttffrddqbqpqzzmttlhhsqhsqhsqhhlplttpnpsstpthhpfhpfplprrgfrgffjppg",
data          b. = "hgppghgdggjmmcgmccjvvsrvsrrwgrgmrrngrgttvbtbltthrthrttmfffjpfpssncnrngrrltrltlggjgcgllrzzllhwwjwrwgr",
data          b. = "gsrgrhrphrhhqwqsqmqlmqlmqmqrmrnnwnhwnhwhzwzjwwgbghhsjspszsznzfztfzfpzzlczztctsstctqtfqfqcfqqjrjccttm",
data          b. = "qqfpfdfnfwnffqbfbblpbpfpcfcwfccblbwwmqwqrrgprpccngnhghpppwmpplcppfrfjjgmmbzbhbcbzzgdgsdsvvqllzlppnfn",
data          b. = "lnlslsljlppcscqqfjjjwzwppfgfjjsvvsggjbjljpjpzjzrzjrzzfnfpnfpnfpnfnsnggmpggdllpmmrhhdqqppttgqqcsqsjsb",
data          b. = "jsjrsrqrbqqmbmcbmcbmbfmfvfqqdbdppmrprnrggmjjhnhbhdhbbfcbbcjjdhhwjwmjmssjswscswcwzccbgbqqmqgmmsdsjsbs",
data          b. = "dbsddvttjpppcqpcqcgqqslqsssczszrzvvrtvvjppswwhnnwlwtwhhwwzfwwpfpddlvvnvnnvlnntjtqjqjzzjttvvbqbhqbhbb",
data          b. = "bwnwhnwnppdbpdpvddrqdqjjlvlqqdfdhdjhdjdcjcrcjjggfmfvvfllvfvgglzllmhmzmdddfwddqjjqjfqfcfrrstrssptsstl",
data          b. = "lrflfwwgswslwlbwbwjjvhvfvhfhffhsslwsllbnbblccbwwjqwqqdllrdrnrnffcbbqqpnqqdmdndtntvvrjvvsvmvgvnnmjlwg",
data          b. = "njcwljgwnrwpqlztwrpmpgqtwlhrcwsrrhqhjhznrtpqfdnzbfqrzwslptdbdcnqvcllpjsfdvmzqwvzbpnmfcfcjnbmhtwhttjg",
data          b. = "tnczwctpdthhwmzvzrrgsnmbflgmszgsbvghbzgcmcmszgsbfmlmpbdspqlftmqrcjtmvgcrzznlfwjcbmddplsqrfflqnqfsldw",
data          b. = "hnncczdmfrrrsbjjqsdzrsgbdbwjbslfcqglsqfddhdsrcdrgqfqthgmfjvnfdfgdncfzpvqcpscnpmfgvqbfwszwzgmqvmcrdrw",
data          b. = "plfshdgqrchmccpqfznbmfvlhdpctlqgjslrwhjfjlmqfblgjrdlnzdtwlpwhnrhrcrpfwqpmjlgrdbgpbljntmbqlblqqqpgrnj",
data          b. = "tmjqvjpzvsqdpgtchmmwbhtmgcjqdplrtptqcvdjjpqdzsrcjhcwvdcghlwrdhtdfctmqfcjcqhcvvbzgsvlggcrdgqbtznwwmnb",
data          b. = "gsfrjprqgcmlswftlwpqqqvshdprldrsghmhrqvmqmvglbvzpvtrjbhcvhqmvdtcvsllznqzjmhpnlbhmlzthbwwhhvdtcdfdcdz",
data          b. = "hnbsrnqqjvzzsvfjhbsdlsbdlqjnlpnhfcjtdppzmphghltztzcdvzwbftbvwhvgmrllqfzrpbltptdtjjqtfwjfmczzgdvclqbs",
data          b. = "bftgtlhnhrrvbpvdltstdnhqvpvtjhmghptvsfnlspslmfsftzdrwljrgblgmcbmlszmhnlfdtmsrnjqwrfmsnfgpcqgzmlwppff",
data          b. = "rmbvhnlstfpgzwwmwffrqpdfvrspbczbrclwljgzfhpsrwwpdndfgjwbjtftnjrqvmtmzvjmtlmjhhptmgjvfrlzncmhnmpfcwpj",
data          b. = "bcpftqfzvmtldqhjpwvzrdnvnwnscgzslvfgjjpcvjshctmmpjbgdwtdjtlmztsbmwrjtmltnlsmwmjnpcgpprnfwcqdldbbqbfm",
data          b. = "dnvprzqwvntgzdbrsgdpgdjbcblmqpdphmwgvbgwlpblflphvjgjsjfshbjdftcqmsdnrzbgngcvddddjvrndhdcscqqswrnvslf",
data          b. = "rlvvncqjhzlbhdqhtrlvdsvjsbglhfzfphmzfmzqdvjqdwhjgfdwmzsdmbjzstjddfmfqjhmbdgdbvvhbqgstrzpvhpthhbwljcz",
data          b. = "zrmvgsmbqvzdrmhvvjlmphzjfbmfqvwhtnrlfnfmqnnjvnwjswzshwgljmfjhrwbwgtpdqnqgqdzbssbjfbsgwmfzpfjdrtrnmsd",
data          b. = "ffhnbgnrdlbjzfjrvtjgjgcvvzgllljrcrshczvpfqgnwnjjnhbwgvzwrptrgrdgtczjfzzndsqhqpmtqsvmcncfszsjllzzsjjm",
data          b. = "wgplpjwlhnhgbhctrttgzqbbcflzqvqgmhgdtlvfpbtncbwsjgnzpmbspcqzzwfplfprqlnbctwwrzpjtpfrmnpvnjrjppqrzjrc",
data          b. = "mggfmhrstzhmsjllcgjhwrbhcrvdvgmvjqqgmczlmhstmthzphlvrrvqmhjzzfzbhphstflhfjdlwqvzlsszctrdchwjssdfjjfz",
data          b. = "szlqdtwwthfjdqprpfftgdrpdhhcsdcpjbhdrgzwbgjspmffcmgcjnpmwsqwsvpfwzddlcpvlgpvctrssghndhvdmmmgndcjvhdj",
data          b. = "wttqphsjpgfbsdczmplfpwpzzjlbhrjptmsshfttnmhzdzmjctbltqjmfnpndqgwjzwdwrgdjdmcbtvjqwjngrtbfrwcttpdvcqt",
data          b. = "wqndznbchjqcqttrhjpjgwdbwzvwgmdsdfmpdwctvntvnsdmfnznfrsdcllpgpnstrrfrwrfrwnhbclnqhltrcdwqwzzldgbbtzm",
data          b. = "cvnbzmwcmntqpbscqrpzcjnbgbrzpcrcmdmdfsfgdpmgvwccqjrltrgfvjdgbhjndnmtnjjhzvghscdhnhflwplrqdzrnlnsvrtr",
data          b. = "dnphgqwjwqcjvtfdfshqdwbsvgrqbdlncjmhdmrlsvdnrhztznczzllsvpqlvwgqjvgvvwgrjcvtjvhrsgbdgvlmmtjbwrnftzph",
data          b. = "nqslcpggztgsdbjsbdtzwprsbcljpbwjhcrffnvtplcdlgmbtcgbllbdmwhwcllbqstnqqvdbcjrglwbmcfqvlvtpqncbspbphfl",
data          b. = "vvrrsprlhqspfmqrsdtdlftsfzrqwdfffbhccvpfdtlptqzllfsbbrfnhjgwhlfcwmmjgjndcwfhdzvvvrzmwllthwsdmbbsrfrz",
data          b. = "mqnlnqnjnfpgfvrhsbzhjftmvzrzpqpmlcbnwmbssmvssmmqpvwnsjppdhmnhpntlvqmjnbmtvjnmtbpbzrcfhjfhvztnwrmthbs",
data          b. = "wwthjddjmsdnjmzhhpjdllgscdrgmhfpljfzsmszqsqqgrznddhfmstzdcqpgztgwwqpvrghtmqlgdddlqqwwwtnpldbqtf"

dim d(15) b.

stime = time
exec find_marker 4
etime = time - stime
? "part1: "; etime/60; "s"

stime = time
exec find_marker 14
etime = time - stime
? "part2: "; etime/60; "s"

proc find_marker seq_len
  p_start = 1
  found_seq = 0
  while found_seq = 0
    exec unique_count p_start
    inc p_start
  wend
endproc

proc unique_count s
  has_c = 0
  for i = 1 to seq_len
    ' the data is stored as <len>, len * real bytes
    ' so have to skip the length values to get a real index
    idx = i + s + (i+s-1)/100
    ' copy into our buffer
    d(i) = inp_dt(idx)
    ' and test if we have just insert same as a previous entry
    for j = 1 to i - 1
      if d(i) = d(j)
        has_c = 1
      endif
      if has_c
        exit
      endif
    next j
    if has_c
      exit
    endif
  next i
  if not has_c
    ? "marker at: "; s + seq_len
    found_seq = 1
  else
    found_seq = 0
  endif
endproc
