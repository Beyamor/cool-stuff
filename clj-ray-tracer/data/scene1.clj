{:scene {:objects [{:shape #tracer/sphere {:center [0 7 -20]
                                           :radius 5}
                    :color #tracer/color "red"}

                   {:shape #tracer/sphere {:center [20 13 -30]
                                           :radius 5}
                    :color #tracer/color "blue"}

                   {:shape #tracer/sphere {:center [-10 5 -15]
                                           :radius 2.5}
                    :color #tracer/color "green"}

                   {:shape #tracer/plane {:point [0 0 0]
                                          :normal [0 1 -0.01]}
                    :color #tracer/color "white"}]

         :lights [{:position #math/vector [-5 30 0]
                   :color #tracer/color "white"}]}

 :view {:width 800
        :height 600
        :eye {:position #math/vector [0 5 0]}}

 :parameters {:reflection-depth 4
              :antialiasing true
              :k {:ambient 0.1
                  :specular 0.45
                  :diffuse 0.45}}}
